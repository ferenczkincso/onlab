package com.example.todoapp.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun MenuDrawer(
    onTaskListClick: () -> Unit,
    onPomodoroClick: () -> Unit,
    onDoneTaskListClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            DrawerTaskItem(
                title = "Task List",
                onClick = onTaskListClick,
                icon = Icons.Default.List
            )
        }
        item {
            DrawerTaskItem(
                title = "Pomodoro counter",
                onClick = onPomodoroClick,
                icon = Icons.Filled.Timer
            )
        }
        item {
            DrawerTaskItem(
                title = "Done tasks list",
                onClick = onDoneTaskListClick,
                icon = Icons.Default.Checklist
            )
        }
        item {
            DrawerTaskItem(
                title = "Calendar",
                onClick = onCalendarClick,
                icon = Icons.Default.CalendarMonth
            )
        }
        item {
            DrawerTaskItem(
                title = "Invitations",
                onClick = onLogoutClick,
                icon = Icons.Default.InsertInvitation
            )
        }
        item {
            DrawerTaskItem(
                title = "Log out",
                onClick = onLogoutClick,
                icon = Icons.Default.Logout
            )
        }
    }
}

@Composable
fun DrawerTaskItem(
    title: String,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                width = 2.dp,
                color = Color(0xFF8B7E74),
                shape = MaterialTheme.shapes.medium
            )
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF8B7E74)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF8B7E74),
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}
