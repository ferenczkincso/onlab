package com.example.todoapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.firebase.FirebaseService
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseService: FirebaseService
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    // Felhasználó bejelentkezés email és jelszó alapján
    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = firebaseService.login(email, password)
            _isLoading.value = false
            _isUserLoggedIn.value = result
            if(isUserLoggedIn())
            {
                Log.d("LoginViewModel", "User logged in successfully")
            }
        }
    }

    // Felhasználó regisztráció email és jelszó alapján
    fun register(email: String, password: String, onResult: (AuthResult?, Exception?) -> Unit) {
        viewModelScope.launch {
            val result = firebaseService.register(email, password)
            if (result != null) {
                onResult(result, null) // Sikeres regisztráció
            } else {
                onResult(null, Exception("Registration failed")) // Hiba történt
            }
        }
    }
    fun logout() {
        viewModelScope.launch {
            firebaseService.logout()
            _isUserLoggedIn.value = false
        }
    }

    // Ellenőrzi, hogy a felhasználó be van-e jelentkezve
    fun isUserLoggedIn(): Boolean {
        return firebaseService.isUserLoggedIn()
    }
}
