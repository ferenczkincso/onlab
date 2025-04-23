package com.example.todoapp.domain.state

sealed class LoginState {
    object LoggedIn : LoginState()
    object LoggedOut : LoginState()
    object Loading : LoginState()
    data class LoggingError(val message: String) : LoginState()
}