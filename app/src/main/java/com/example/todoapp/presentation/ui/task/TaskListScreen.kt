package com.example.todoapp.presentation.ui.task

import android.content.res.Resources
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.collectAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.todoapp.data.model.Task
import com.example.todoapp.presentation.viewmodel.TaskViewModel
import com.example.todoapp.presentation.ui.task.TaskItem


import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel = hiltViewModel(),
    onPomodoroClick: () -> Unit,
    onLogout: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Kilépési megerősítő ablak
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Kilépés megerősítése") },
            text = { Text("Biztosan ki szeretne lépni?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onLogout()
                }) {
                    Text("Igen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Nem")
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onPomodoroClick = onPomodoroClick,
                onLogoutClick = { showDialog = true }
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Open Drawer")
            }

            LazyColumn {
                items(viewModel.tasks.value) { task ->
                    TaskItem(
                        task = task,
                        onTaskClick = { /* Implement task click action */ },
                        onTaskCompleteToggle = { /* Implement complete toggle action */ }
                    )
                }
            }
        }
    }
}

@Composable
fun DrawerContent(
    onPomodoroClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TextButton(onClick = onPomodoroClick) {
            Text("Váltás Pomodoro-ra")
        }
        TextButton(onClick = onLogoutClick) {
            Text("Kilépés")
        }
    }
}




