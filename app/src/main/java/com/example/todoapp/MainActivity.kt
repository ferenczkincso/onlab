package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todoapp.data.firebase.FirebaseService
import com.example.todoapp.domain.state.TaskState
import com.example.todoapp.presentation.ui.auth.LoginScreen
import com.example.todoapp.presentation.ui.auth.RegisterScreen
import com.example.todoapp.presentation.ui.calendar.CalendarScreen
import com.example.todoapp.presentation.ui.pomodoro.PomodoroTimerScreen
import com.example.todoapp.presentation.ui.task.AddTaskScreen
import com.example.todoapp.presentation.ui.task.DoneTaskListScreen
import com.example.todoapp.presentation.ui.task.TaskListScreen
import com.example.todoapp.presentation.viewmodel.TaskViewModel
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var firebaseService: FirebaseService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            val navController = rememberNavController()
            val viewModel: TaskViewModel = hiltViewModel()
            var isUserLoggedIn by remember {
                mutableStateOf(firebaseService.isUserLoggedIn())
            }

            LaunchedEffect(isUserLoggedIn) {
                if (isUserLoggedIn) {
                    viewModel.handleIntent(com.example.todoapp.domain.intent.TaskIntent.LoadTodos)
                }
            }

            val state by viewModel.state.collectAsState()

            NavHost(
                navController = navController,
                startDestination = if (isUserLoggedIn) "taskList" else "login"
            ) {
                composable(
                    "login",
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300))
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(300)
                        ) + fadeOut(animationSpec = tween(300))
                    }
                ) {
                    LoginScreen(
                        navController = navController,
                        onLoginSuccess = { isUserLoggedIn = true }
                    )
                }
                composable(
                    "register",
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300))
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(300)
                        ) + fadeOut(animationSpec = tween(300))
                    }
                ) {
                    RegisterScreen(
                        navController = navController,
                        onRegisterSuccess = {
                            isUserLoggedIn = true
                        }
                    )
                }
                composable(
                    "taskList",
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300))
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(300)
                        ) + fadeOut(animationSpec = tween(300))
                    }
                ) {
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
                        onDoneTaskListClick = {navController.navigate("doneTaskList")},
                        onCalendarClick = {navController.navigate("calendar")},
                        onTaskEditClick = {task ->navController.navigate("addTask?taskId=${task.id}")}
                    )
                }

                composable(
                    route = "addTask?taskId={taskId}",
                    arguments = listOf(navArgument("taskId") {
                        type = NavType.StringType
                        defaultValue = ""
                    }),
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300))
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(300)
                        ) + fadeOut(animationSpec = tween(300))
                    }
                ) { backStackEntry ->
                    val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                    val taskViewModel: TaskViewModel = hiltViewModel()
                    val taskState by taskViewModel.state.collectAsState()
                    val existingTask = (taskState as? TaskState.TasksLoaded)?.let { it.activeTasks + it.completedTasks }
                        ?.find { it.id == taskId }

                    AddTaskScreen(
                        onSaveTask = { navController.popBackStack() },
                        viewModel = taskViewModel,
                        firebaseService = firebaseService,
                        existingTask = existingTask
                    )
                }

                composable(
                    "pomodoro",
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300))
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(300)
                        ) + fadeOut(animationSpec = tween(300))
                    }
                ) {
                    PomodoroTimerScreen(
                        onPomodoro = {  },
                        onLogout = { isUserLoggedIn = false
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            } },
                        onTaskList = {navController.navigate("taskList")  },
                        onPopBackStack = { navController.popBackStack() },
                        onDoneTaskListClick = { navController.navigate("doneTaskList") },
                        onCalendarClick = {navController.navigate("calendar")}
                    )
                }
                composable(
                    "doneTaskList",
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300))
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(300)
                        ) + fadeOut(animationSpec = tween(300))
                    }
                ) {
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
                        onTaskListClick = { navController.navigate("taskList") },
                        onCalendarClick = {navController.navigate("calendar")}
                    )
                }
                composable(
                    "calendar",
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300))
                    },
                    exitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { -it },
                            animationSpec = tween(300)
                        ) + fadeOut(animationSpec = tween(300))
                    }
                ) {
                    CalendarScreen(
                        onAddTaskClick = { navController.navigate("addTask") },
                        firebaseService = firebaseService,
                        onPomodoroClick = { navController.navigate("pomodoro") },
                        onLogout = {
                            isUserLoggedIn = false
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onTaskListClick = { navController.navigate("taskList") },
                        onDoneTaskListClick = { navController.navigate("doneTaskList") },
                        onCalendarClick = { navController.navigate("calendar") }
                    )
                }
            }
        }
    }
}
