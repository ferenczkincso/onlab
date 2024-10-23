package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.presentation.ui.login.LoginScreen
import com.example.todoapp.presentation.ui.pomodoro.PomodoroTimerScreen
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "login") {
                composable("login") {
                    TaskApp(navController) // Pass the navController to TaskApp
                }
                composable("pomodoro") {
                    PomodoroTimerScreen()
                }
            }
        }
    }
}