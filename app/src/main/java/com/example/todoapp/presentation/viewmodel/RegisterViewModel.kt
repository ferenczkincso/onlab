package com.example.todoapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.firebase.FirebaseService
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseService: FirebaseService
) : ViewModel() {

    fun register(email: String, password: String, onResult: (AuthResult?, Exception?) -> Unit) {
        viewModelScope.launch {
            val result = firebaseService.register(email, password)
            if (result != null) {
                onResult(result, null)
            } else {
                onResult(null, Exception("Registration failed"))
            }
        }
    }
    fun login(email: String, password: String, onResult: (Boolean, Exception?) -> Unit) {
        viewModelScope.launch {
            val isSuccess = firebaseService.login(email, password)
            if (isSuccess) {
                onResult(true, null)
            } else {
                onResult(false, Exception("Login failed"))
            }
        }
    }
}
