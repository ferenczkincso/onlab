package com.example.todoapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.firebase.FirebaseService
import com.example.todoapp.domain.intent.LoginIntent
import com.example.todoapp.domain.state.LoginState
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseService: FirebaseService
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.LoggedOut)
    val loginState: StateFlow<LoginState> = _loginState

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.Login -> login(intent.email, intent.password)
            is LoginIntent.Logout -> logout()
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

    private fun logout() {
        firebaseService.auth.signOut()
        _loginState.value = LoginState.LoggedOut
    }
}

