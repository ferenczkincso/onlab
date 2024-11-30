package com.example.todoapp.presentation.ui.task

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.todoapp.domain.state.TaskState
import com.example.todoapp.presentation.viewmodel.TaskViewModel
import java.lang.reflect.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todoapp.domain.intent.TaskIntent
import com.example.todoapp.presentation.ui.theme.customTypography
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoneTaskListScreen(
    navController: NavController,
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    onPomodoroClick: () -> Unit,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    MaterialTheme(
        typography = customTypography,
    ) {
        Surface(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
            var showDialog by remember { mutableStateOf(false) }
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "Confirm logout") },
                    text = { Text("Are you sure you want to log out?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                                viewModel.logout()
                                onLogout()
                            },
                            colors = ButtonDefaults.buttonColors(Color.DarkGray)
                        ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDialog = false },
                            colors = ButtonDefaults.buttonColors(Color.DarkGray)
                        ) {
                            Text("No")
                        }
                    }
                )
            }

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    DrawerContent(
                        onPomodoroClick = onPomodoroClick,
                        onLogoutClick = { showDialog = true },
                        onTaskListClick = { scope.launch { drawerState.close() } },
                        onDoneTaskListClick = { scope.launch { drawerState.close() } }
                    )
                },
                scrimColor = MaterialTheme.colorScheme.surface.copy(alpha = 1f)
            ) {
                Column(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                    TaskListHeader(onDrawerOpen = { scope.launch { drawerState.open() } })
                    Box(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = androidx.compose.ui.Modifier
                                .fillMaxSize()
                                .padding(top = 0.dp)
                        ) {
                            when (state) {
                                is TaskState.Loading -> {
                                    item {
                                        CircularProgressIndicator(modifier = androidx.compose.ui.Modifier.align(Alignment.Center))
                                    }
                                }
                                is TaskState.TasksLoaded -> {
                                    val tasksLoaded = state as TaskState.TasksLoaded
                                    items(tasksLoaded.completedTasks) { task ->
                                        TaskItem(
                                            task = task,
                                            onTaskClick = {
                                                viewModel.sendIntent(TaskIntent.UpdateTaskStatus(task.id, !task.completed))
                                            },
                                            onTaskCompleteToggle = { completed ->
                                                viewModel.sendIntent(TaskIntent.UpdateTaskStatus(task.id, completed))
                                            }
                                        )
                                    }
                                }
                                is TaskState.Error -> {
                                    val errorMessage = (state as TaskState.Error).message
                                    Log.d("TaskListScreen", "Error loading tasks: $errorMessage")
                                }
                                else -> { }
                            }
                        }
                    }
                }
            }
        }
    }
}


