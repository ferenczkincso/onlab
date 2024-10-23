package com.example.todoapp.presentation.ui.task

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.example.todoapp.data.model.Priority
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoapp.data.model.Task
import com.google.firebase.Timestamp

@Composable
fun TaskItem(
    task: Task,
    onTaskClick: (Task) -> Unit,
    onTaskCompleteToggle: (Task) -> Unit
) {
    Card(
        colors = CardColors(
            (when (task.priority) {
                Priority.HIGH -> {
                    Color(0xFF8B7E74)
                }
                Priority.MEDIUM -> {
                    Color(0xFFC7BCA1)
                }
                Priority.LOW -> {
                    Color(0xFFF1D3B3)
                }
                else -> {
                    Color(0xFFe6e1d5)
                }
            }),
            Color.White,
            Color.White,
            Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable { onTaskClick(task) },

        shape = RoundedCornerShape(5.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .background(
                    when (task.priority) {
                        Priority.HIGH -> {
                            Color(0xFF8B7E74)
                        }
                        Priority.MEDIUM -> {
                            Color(0xFFC7BCA1)
                        }
                        Priority.LOW -> {
                            Color(0xFFF1D3B3)
                        }
                        else -> {
                            Color(0xFFe6e1d5)
                        }
                    }
                ), Arrangement.Start, Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Checkbox(
                checked = task.isCompleted,
                enabled = true,
                onCheckedChange = { onTaskCompleteToggle(task.copy(isCompleted = it)) },
                colors = CheckboxDefaults.colors(Color.White)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Priority: ${task.priority}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
            }


        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun TaskItemPreview() {
    TaskItem(
        task = Task(
            id = "id1",
            title = "Buy groceries",
            description = "Milk, bread, eggs",
            dueDate = Timestamp.now(),
            priority = Priority.HIGH,
            isCompleted = false,
        ),
        onTaskClick = {},
        onTaskCompleteToggle = {}
    )
}
