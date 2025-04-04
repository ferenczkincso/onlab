package com.example.todoapp.domain.intent

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task

sealed class LoginIntent {
    data class Login(val email: String, val password: String) : LoginIntent()
    data class GoogleSignIn(val task: Task<GoogleSignInAccount>) : LoginIntent()
    object Logout : LoginIntent()
}
