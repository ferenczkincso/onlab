package com.example.todoapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todoapp.presentation.ui.login.LoginScreen
import com.example.todoapp.presentation.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavController
import com.example.todoapp.presentation.ui.task.TaskListScreen
import dagger.hilt.android.HiltAndroidApp

@Composable
fun TaskApp(navController: NavController) { // Add navController parameter
    val viewModel: TaskViewModel = hiltViewModel()
    var isUserLoggedIn by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }

    if (isUserLoggedIn) {
        // Megjelenítjük a to-do lista képernyőt, ha a felhasználó hitelesítve van
        TaskListScreen(viewModel, onPomodoroClick = {0}, onLogout = {0})
    } else {
        // Megjelenítjük a bejelentkezési képernyőt, ha a felhasználó nincs hitelesítve
        LoginScreen(navController = navController, onLoginSuccess = {
            isUserLoggedIn = true
        })
    }
}
