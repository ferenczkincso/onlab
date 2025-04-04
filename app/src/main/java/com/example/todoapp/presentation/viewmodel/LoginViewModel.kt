package com.example.todoapp.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.R
import com.example.todoapp.data.firebase.FirebaseService
import com.example.todoapp.domain.intent.LoginIntent
import com.example.todoapp.domain.state.LoginState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseService: FirebaseService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.LoggedOut)
    val loginState: StateFlow<LoginState> = _loginState
    
    private val googleSignInClient: GoogleSignInClient
    
    init {
        Log.d("LoginViewModel", "Initializing LoginViewModel with Google Sign-In")
        
        // Initialize Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }
    
    fun getGoogleSignInClient(): GoogleSignInClient {
        return googleSignInClient
    }

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.Login -> login(intent.email, intent.password)
            is LoginIntent.Logout -> logout()
            is LoginIntent.GoogleSignIn -> handleGoogleSignInResult(intent.task)
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                firebaseService.auth.signInWithEmailAndPassword(email, password).await()
                _loginState.value = LoginState.LoggedIn
            } catch (e: Exception) {
                _loginState.value = LoginState.LoggingError(e.message ?: "Unknown error")
            }
        }
    }
    
    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading
                
                val account = completedTask.getResult(ApiException::class.java)
                Log.d("LoginViewModel", "Google Sign-In successful. Account ID: ${account.id}")
                
                // Get the ID token from the Google account
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                
                // Sign in with Firebase using the Google credential
                val authResult = firebaseService.auth.signInWithCredential(credential).await()
                
                if (authResult.user != null) {
                    Log.d("LoginViewModel", "Firebase auth successful. UID: ${authResult.user?.uid}")
                    _loginState.value = LoginState.LoggedIn
                } else {
                    Log.e("LoginViewModel", "Firebase auth failed. User is null.")
                    _loginState.value = LoginState.LoggingError("Firebase authentication failed")
                }
                
            } catch (e: ApiException) {
                Log.e("LoginViewModel", "Google sign-in failed", e)
                _loginState.value = LoginState.LoggingError("Google Sign-In failed: ${e.statusCode}")
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Firebase auth error", e)
                _loginState.value = LoginState.LoggingError("Authentication error: ${e.message}")
            }
        }
    }

    private fun logout() {
        firebaseService.auth.signOut()
        googleSignInClient.signOut()
        _loginState.value = LoginState.LoggedOut
    }
}
