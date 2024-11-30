package com.example.todoapp.presentation.ui.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.presentation.ui.theme.customBlue


@Composable
fun TaskListHeader(onDrawerOpen: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = customBlue)
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onDrawerOpen() }) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Open Drawer",
                tint = Color.White
            )
        }
        Text(
            text = "TaskTask",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontSize = 30.sp,
            modifier = Modifier
                .weight(2f),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun TaskListHeaderPreview() {
    TaskListHeader(onDrawerOpen = {})
}

