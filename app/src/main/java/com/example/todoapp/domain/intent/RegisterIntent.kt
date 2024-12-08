package com.example.todoapp.domain.intent

sealed class RegisterIntent {
    data class Register(val email: String, val password: String) : RegisterIntent()
}
