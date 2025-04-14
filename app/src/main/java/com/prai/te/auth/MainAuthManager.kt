package com.prai.te.auth

import android.content.Context
import android.content.Intent
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
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.prai.te.common.MainLogger
import kotlin.coroutines.resume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
                MainLogger.Auth.log("Google sign in failed: ${exception.statusCode}")
                when (exception.statusCode){
                    12501 -> scope.launch { mutableEvent.emit(Event.Cancel) } // LOGIN 취소
                    12502 -> scope.launch { mutableEvent.emit(Event.Error) } // LOGIN 중에, 다시 로그인
                    else -> scope.launch { mutableEvent.emit(Event.Error) }
                }
            }
        }
    }

    fun connect(context: Context) {
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

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val idToken = account.idToken
        if (idToken == null) {
            MainLogger.Auth.log("firebaseAuthWithGoogle: idToken is null")
            scope.launch { mutableEvent.emit(Event.Error) }
            return
        }

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                scope.launch { mutableEvent.emit(Event.Connect) }
            } else {
                scope.launch { mutableEvent.emit(Event.Error) }
            }
            MainLogger.Auth.log("firebaseAuthWithGoogle: ${task.isSuccessful}")
        }
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
                scope.launch { mutableEvent.emit(Event.Error) }
                MainLogger.Auth.log("disconnect: exception: $exception")
            }
        }
    }

    sealed interface Event {
        data object Connect : Event
        data object Disconnect : Event
        data object Error : Event
        data object Cancel : Event
        data object NoCredential : Event
    }

    companion object {
        private const val SERVER_CLIENT_ID =
            "704215598307-57sh6dabf9j5b4tk7m39mv5s9kkbtdfu.apps.googleusercontent.com"

        fun isConnected(): Boolean {
            return FirebaseAuth.getInstance().currentUser != null
        }
    }
}
