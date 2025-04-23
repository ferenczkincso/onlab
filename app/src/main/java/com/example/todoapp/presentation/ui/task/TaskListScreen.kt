package com.example.todoapp.presentation.ui.task
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.todoapp.data.model.Task
import com.example.todoapp.domain.intent.TaskIntent
import com.example.todoapp.domain.state.TaskState
import com.example.todoapp.presentation.ui.DrawerTaskItem
import com.example.todoapp.presentation.ui.MenuDrawer
import com.example.todoapp.presentation.ui.theme.customTypography
import com.example.todoapp.presentation.viewmodel.TaskViewModel
import kotlinx.coroutines.launch


@Composable
fun TaskListScreen(
    navController: NavController,
    viewModel: TaskViewModel = hiltViewModel(),
    onPomodoroClick: () -> Unit,
    onLogout: () -> Unit,
    onAddTaskClick: () -> Unit,
    onDoneTaskListClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onTaskEditClick: (Task) ->Unit
) {
    val state by viewModel.state.collectAsState()

    MaterialTheme(
        typography = customTypography,
    ) {
        Surface(modifier = Modifier.fillMaxSize(),
            color = Color(0xFFFFFFFF)) {
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
                scrimColor = MaterialTheme.colorScheme.surface.copy(alpha = 1f),
                        drawerContent = {
                    MenuDrawer (
                        onPomodoroClick = onPomodoroClick,
                        onLogoutClick = { showDialog = true },
                        onTaskListClick = { scope.launch { drawerState.close() } },
                        onDoneTaskListClick = onDoneTaskListClick,
                        onCalendarClick = onCalendarClick
                    )
                }
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    TaskListHeader(onDrawerOpen = { scope.launch { drawerState.open() } })
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 0.dp)
                        ) {
                            when (state) {
                                is TaskState.Loading -> {
                                    item {
                                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                    }
                                }
                                is TaskState.TasksLoaded -> {
                                    val tasksLoaded = state as TaskState.TasksLoaded
                                    Log.d("TaskListScreen", "Loaded tasks: ${tasksLoaded.activeTasks}")
                                    items(tasksLoaded.activeTasks) { task ->
                                        TaskItem(
                                            task = task,
                                            onTaskCompleteToggle = { updatedTask ->
                                                Log.d("TaskListScreen", "Marking task as completed")
                                                viewModel.handleIntent(TaskIntent.UpdateTaskStatus(updatedTask.id, updatedTask.completed))
                                            },
                                            onDeleteClick = { viewModel.deleteTask(task.id)},
                                            onEditClick = {onTaskEditClick(task)}
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

                        FloatingActionButton(
                            onClick = onAddTaskClick,
                            shape = CircleShape,
                            contentColor = Color.White,
                            containerColor = Color(0xFF65647C),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
                        }
                    }
                }
            }
        }
    }
}




