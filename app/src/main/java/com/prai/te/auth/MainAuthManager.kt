package com.prai.te.auth

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.prai.te.common.MainLogger
import kotlin.coroutines.resume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

@Suppress("DEPRECATION")
internal class MainAuthManager(private val scope: CoroutineScope) {
    val event by lazy { mutableEvent.asSharedFlow() }

    private val mutableEvent = MutableSharedFlow<Event>()

    private var googleSignInClient: GoogleSignInClient? = null
    private var signInLauncher: ActivityResultLauncher<Intent>? = null

    fun initialize(activity: ComponentActivity) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(SERVER_CLIENT_ID)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)

        signInLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (exception: ApiException) {
                MainLogger.Auth.log(exception, "Google sign in failed: ${exception.statusCode}")
                when (exception.statusCode){
                    12501 -> scope.launch { mutableEvent.emit(Event.Cancel) } // LOGIN 취소
                    12502 -> scope.launch { mutableEvent.emit(Event.Error) } // LOGIN 중에, 다시 로그인
                    else -> scope.launch { mutableEvent.emit(Event.Error) }
                }
            }
        }
    }

    fun connect(context: Context) {
        if (!isNetworkAvailable(context)) {
            MainLogger.Auth.log("Network unavailable")
            scope.launch { mutableEvent.emit(Event.NetworkError) }
            return
        }

        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null && account.idToken != null) {
            firebaseAuthWithGoogle(account)
            return
        }

        val signInIntent = googleSignInClient?.signInIntent ?: return
        signInLauncher?.launch(signInIntent) ?: run {
            MainLogger.Auth.log("signInLauncher is not initialized")
            scope.launch { mutableEvent.emit(Event.Error) }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount, retryCount: Int = 0) {
        val idToken = account.idToken
        if (idToken == null) {
            MainLogger.Auth.log("firebaseAuthWithGoogle: idToken is null")
            scope.launch { mutableEvent.emit(Event.Error) }
            return
        }

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.signInWithCredential(credential)
            .addOnSuccessListener {
                scope.launch { mutableEvent.emit(Event.Connect) }
                MainLogger.Auth.log("firebaseAuthWithGoogle: success")
            }
            .addOnFailureListener { exception ->
                MainLogger.Auth.log(exception, "firebaseAuthWithGoogle failed: ${exception.message}")

                when {
                    exception is FirebaseAuthInvalidCredentialsException && retryCount < MAX_RETRY_COUNT -> {
                        MainLogger.Auth.log("Retrying authentication (${retryCount + 1}/$MAX_RETRY_COUNT)")
                        refreshCredentialAndRetry(retryCount)
                    }
                    else -> {
                        scope.launch { mutableEvent.emit(Event.AuthError(exception)) }
                    }
                }
            }
    }

    private fun refreshCredentialAndRetry(retryCount: Int = 0) {
        MainLogger.Auth.log("Attempting to refresh credentials (attempt: ${retryCount + 1})")

        // 최대 재시도 횟수 확인
        if (retryCount >= MAX_RETRY_COUNT) {
            MainLogger.Auth.log("Max retry count reached, giving up")
            scope.launch { mutableEvent.emit(Event.Error) }
            return
        }

        // 기존 인증 정보 삭제
        scope.launch {
            try {
                // 코루틴으로 Task 처리
                val signOutSuccessful = suspendCancellableCoroutine<Boolean> { continuation ->
                    googleSignInClient?.signOut()
                        ?.addOnSuccessListener {
                            continuation.resume(true)
                        }
                        ?.addOnFailureListener { e ->
                            MainLogger.Auth.log(e, "Sign out failed")
                            continuation.resume(false)
                        }
                        ?: run {
                            MainLogger.Auth.log("googleSignInClient is null")
                            continuation.resume(false)
                        }
                }

                if (signOutSuccessful) {
                    MainLogger.Auth.log("Successfully signed out, clearing Firebase auth")
                    FirebaseAuth.getInstance().signOut()

                    // 잠시 대기 후 새 인증 정보 요청 (API 호출 간격 확보)
                    delay(500)

                    // 새로운 인증 정보 요청
                    try {
                        val newAccount = suspendCancellableCoroutine<GoogleSignInAccount?> { continuation ->
                            googleSignInClient?.silentSignIn()
                                ?.addOnSuccessListener { account ->
                                    continuation.resume(account)
                                }
                                ?.addOnFailureListener { e ->
                                    MainLogger.Auth.log(e, "Silent sign-in failed")
                                    continuation.resume(null)
                                }
                                ?: run {
                                    MainLogger.Auth.log("googleSignInClient is null")
                                    continuation.resume(null)
                                }
                        }

                        if (newAccount != null) {
                            MainLogger.Auth.log("Silent sign-in successful, retrying with new credentials")
                            firebaseAuthWithGoogle(newAccount, retryCount + 1)
                        } else {
                            // 자동 로그인 실패 시 명시적 로그인 시도
                            MainLogger.Auth.log("Silent sign-in returned null account, requesting explicit sign-in")
                            requestExplicitSignIn()
                        }
                    } catch (e: Exception) {
                        MainLogger.Auth.log(e, "Exception during silent sign-in")
                        requestExplicitSignIn()
                    }
                } else {
                    // 로그아웃 실패 시 명시적 로그인 시도
                    MainLogger.Auth.log("Sign out failed, requesting explicit sign-in")
                    requestExplicitSignIn()
                }
            } catch (e: Exception) {
                MainLogger.Auth.log(e, "Exception during credential refresh")
                scope.launch { mutableEvent.emit(Event.Error) }
            }
        }
    }

    private fun requestExplicitSignIn() {
        val signInIntent = googleSignInClient?.signInIntent
        if (signInIntent != null && signInLauncher != null) {
            MainLogger.Auth.log("Launching explicit sign-in")
            signInLauncher?.launch(signInIntent)
        } else {
            MainLogger.Auth.log("Cannot launch sign-in intent")
            scope.launch { mutableEvent.emit(Event.NoCredential) }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getAuthToken(): String? = suspendCancellableCoroutine { continuation ->
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            continuation.resume(null)
            MainLogger.Auth.log("getAuthToken: failed")
            return@suspendCancellableCoroutine
        }
        user.getIdToken(true).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(task.result?.token)
            } else {
                continuation.resume(null)
                MainLogger.Auth.log("getAuthToken: failed")
            }
        }
    }

    fun disconnect() {
        val client = googleSignInClient ?: return

        scope.launch {
            try {
                client.signOut().addOnCompleteListener {
                    FirebaseAuth.getInstance().signOut()
                    scope.launch { mutableEvent.emit(Event.Disconnect) }
                    MainLogger.Auth.log("disconnect: success")
                }
            } catch (exception: Exception) {
                scope.launch { mutableEvent.emit(Event.SignOutError) }
                MainLogger.Auth.log(exception, "disconnect: exception: $exception")
            }
        }
    }

    sealed interface Event {
        data object Connect : Event
        data object Disconnect : Event
        data object Error : Event
        data object Cancel : Event
        data object NoCredential : Event
        data object NetworkError : Event
        data object SignOutError : Event
        data class AuthError(val exception: Exception) : Event
    }

    companion object {
        private const val SERVER_CLIENT_ID =
            "704215598307-57sh6dabf9j5b4tk7m39mv5s9kkbtdfu.apps.googleusercontent.com"
        private const val MAX_RETRY_COUNT = 2

        fun isConnected(): Boolean {
            return FirebaseAuth.getInstance().currentUser != null
        }
    }
}

