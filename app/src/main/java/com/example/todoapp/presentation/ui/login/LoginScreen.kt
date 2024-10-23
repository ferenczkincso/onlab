package com.example.todoapp.presentation.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.data.firebase.FirebaseService
import com.example.todoapp.presentation.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(), // Közvetlenül a LoginViewModel-t használjuk
    onLoginSuccess: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()

    // Ellenőrizzük, hogy a felhasználó be van-e jelentkezve
    if (isUserLoggedIn) {
        onLoginSuccess() // Ha be van jelentkezve, meghívjuk az onLoginSuccess-t
    }

    // UI elemek
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.login(email, password)
            },
            enabled = !isLoading // Gomb csak akkor aktív, ha nem töltődik
        ) {
            Text(if (isLoading) "Logging in..." else "Log in")
        }
    }
}

class FakeLoginViewModel : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isUserLoggedIn = MutableStateFlow(true) // Assume the user is logged in for the preview
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    fun login(email: String, password: String) {
        // No implementation needed for the preview
    }

    fun register(email: String, password: String) {
        // No implementation needed for the preview
    }

    fun logout() {
        // No implementation needed for the preview
    }
}



