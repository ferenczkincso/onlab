package com.example.todoapp.domain.intent


sealed class LoginIntent {
    data class Login(val email: String, val password: String) : LoginIntent()
    object Logout : LoginIntent()
}
