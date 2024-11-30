package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.data.firebase.FirebaseService
import com.example.todoapp.presentation.ui.auth.LoginScreen
import com.example.todoapp.presentation.ui.auth.RegisterScreen
import com.example.todoapp.presentation.ui.pomodoro.PomodoroTimerScreen
import com.example.todoapp.presentation.ui.task.AddTaskScreen
import com.example.todoapp.presentation.ui.task.DoneTaskListScreen
import com.example.todoapp.presentation.ui.task.TaskListScreen
import com.example.todoapp.presentation.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val viewModel: TaskViewModel = hiltViewModel()
            val firebaseService = FirebaseService()
            var isUserLoggedIn by remember {
                mutableStateOf(FirebaseAuth.getInstance().currentUser != null)
            }

            LaunchedEffect(isUserLoggedIn) {
                if (isUserLoggedIn) {
                        viewModel.loadCompletedTasks()
                    }
            }
            val state by viewModel.state.collectAsState()


            NavHost(
                navController = navController,
                startDestination = if (isUserLoggedIn) "taskList" else "login"
            ) {
                composable("login") {
                    LoginScreen(
                        navController = navController,
                        onLoginSuccess = { isUserLoggedIn = true }
                    )
                }
                composable("register") {
                    RegisterScreen(
                        navController = navController,
                        onRegisterSuccess = {
                            isUserLoggedIn = true
                        }
                    )
                }
                composable("taskList") {
                    TaskListScreen(
                        navController = navController,
                        onPomodoroClick = { navController.navigate("pomodoro") },
                        onLogout = {
                            isUserLoggedIn = false
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onAddTaskClick = { navController.navigate("addTask") },
                        onDoneTaskListClick = {navController.navigate("doneTaskList")}
                    )
                }

                composable("addTask") {
                    AddTaskScreen(
                        onSaveTask = {
                            navController.popBackStack()  // Navigálás vissza a taskList-re mentés után
                        }
                    )
                }

                composable("pomodoro") {
                    PomodoroTimerScreen(
                        onPomodoro = {  },
                        onLogout = { isUserLoggedIn = false
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            } },
                        onTaskList = {navController.navigate("taskList")  },
                        onDoneTaskListClick = { navController.navigate("doneTaskList") }
                    )
                }
                composable("doneTaskList") {
                    DoneTaskListScreen(
                        navController = navController,
                        onPomodoroClick = { navController.navigate("pomodoro") },
                        onLogout = {
                            isUserLoggedIn = false
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onBackClick = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}
