package com.prai.te.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.prai.te.common.MainLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal object MainAuthManager2 {
    private const val SERVER_CLIENT_ID =
        "704215598307-57sh6dabf9j5b4tk7m39mv5s9kkbtdfu.apps.googleusercontent.com"

    val event by lazy { mutableEvent.asSharedFlow() }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val auth = FirebaseAuth.getInstance()
    private val mutableEvent = MutableSharedFlow<Event>()

    fun connect(context: Context) {
        val manager = CredentialManager.create(context)
        val option = GetGoogleIdOption.Builder()
            .setServerClientId(SERVER_CLIENT_ID)
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .build()

//        GetSignInWithGoogleOption(SERVER_CLIENT_ID)
        val option2 = GetSignInWithGoogleOption.Builder(SERVER_CLIENT_ID).build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(option)
            .build()

        scope.launch {
            try {
                val result = manager.getCredential(context, request)
                handleCredentialResult(result.credential)
            } catch (_: NoCredentialException) {
                mutableEvent.emit(Event.NoCredential)
                MainLogger.Auth.log("connect: event: ${Event.NoCredential}")
            } catch (exception: Exception) {
                mutableEvent.emit(Event.Error)
                MainLogger.Auth.log("connect: exception: $exception")
            }
        }
    }

    private fun handleCredentialResult(credential: Credential) {
        if (credential !is CustomCredential) {
            return
        }
        if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            return
        }
        try {
            val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
            val authCredential = GoogleAuthProvider.getCredential(idToken, null)
            Firebase.auth.signInWithCredential(authCredential).addOnCompleteListener { task ->
                val event = if (task.isSuccessful) Event.Connect else Event.Error
                scope.launch { mutableEvent.emit(event) }
                MainLogger.Auth.log("connect: event: $event")
            }
        } catch (exception: Exception) {
            scope.launch { mutableEvent.emit(Event.Error) }
            MainLogger.Auth.log("connect: exception: $exception")
        }
    }

    fun disconnect(context: Context) {
        val type = ClearCredentialStateRequest.TYPE_CLEAR_CREDENTIAL_STATE
        scope.launch {
            try {
                val request = ClearCredentialStateRequest(type)
                FirebaseAuth.getInstance().signOut()
                CredentialManager.create(context).clearCredentialState(request)
                mutableEvent.emit(Event.Disconnect)
                MainLogger.Auth.log("disconnect: success")
            } catch (exception: Exception) {
                mutableEvent.emit(Event.Error)
                MainLogger.Auth.log("disconnect: exception: $exception")
            }
        }
    }

    fun isConnected(): Boolean {
        return auth.currentUser != null
    }

    fun getAuthToken(): String? {
        return auth.currentUser?.uid
    }

    sealed interface Event {
        data object Connect : Event
        data object Disconnect : Event
        data object Error : Event
        data object NoCredential : Event
    }
}