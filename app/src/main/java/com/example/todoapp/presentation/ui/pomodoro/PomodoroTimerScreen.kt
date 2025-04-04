package com.example.todoapp.presentation.ui.pomodoro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todoapp.presentation.ui.task.DrawerContent
import com.example.todoapp.presentation.ui.task.TaskListHeader
import com.example.todoapp.presentation.ui.theme.customTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PomodoroTimerScreen(
    onPomodoro: () -> Unit,
    onLogout: () -> Unit,
    onTaskList: () -> Unit,
    onPopBackStack: () -> Unit,
    onDoneTaskListClick: () -> Unit,
    onCalendarClick: () -> Unit
) {
    var timeLeft by remember { mutableStateOf(30 * 60L) }
    var isRunning by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        if (isRunning && timeLeft > 0) {
            while (timeLeft > 0 && isRunning) {
                delay(1000L)
                timeLeft -= 1
            }
            if (timeLeft == 0L) {
                isRunning = false
            }
        }
    }

    val minutes = (timeLeft / 60).toString().padStart(2, '0')
    val seconds = (timeLeft % 60).toString().padStart(2, '0')

    MaterialTheme(typography = customTypography) {
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Confirm logout") },
                text = { Text("Are you sure you want to log out?") },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        onLogout()
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(
                    onPomodoroClick = onPomodoro,
                    onLogoutClick = { showDialog = true },
                    onTaskListClick = onTaskList,
                    onDoneTaskListClick = onDoneTaskListClick,
                    onCalendarClick = onCalendarClick
                )
            },
            scrimColor = MaterialTheme.colorScheme.surface.copy(alpha = 1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFFFFF)),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TaskListHeader(onDrawerOpen = { scope.launch { drawerState.open() } })

                Text(
                    text = "$minutes:$seconds",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(onClick = {
                    isRunning = !isRunning
                },
                    colors = ButtonDefaults.buttonColors(containerColor = lightColorScheme().secondary)
                )
                {
                    Text(if (isRunning) "Pause" else "Start")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    timeLeft = 30 * 60L
                    isRunning = false
                },
                    colors = ButtonDefaults.buttonColors(containerColor = lightColorScheme().secondary)
                ) {
                    Text("Reset")
                }
            }
        }
    }
}
