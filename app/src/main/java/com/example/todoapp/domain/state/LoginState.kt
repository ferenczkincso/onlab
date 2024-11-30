package com.example.todoapp.domain.state

sealed class LoginState {
    object LoggedIn : LoginState()
    object LoggedOut : LoginState()
}

