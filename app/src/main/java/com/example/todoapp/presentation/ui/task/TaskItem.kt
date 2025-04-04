package com.example.todoapp.presentation.ui.task

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoapp.data.model.Priority
import com.example.todoapp.data.model.Task
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun TaskItem(
    task: Task,
    onTaskCompleteToggle: (Task) -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formattedDate = task.dueDate?.toDate()?.let { dateFormat.format(it) } ?: "No due date"

    Card(
        colors = CardColors(
            containerColor = when (task.priority) {
                Priority.HIGH -> Color(0xFF8B7E74)
                Priority.MEDIUM -> Color(0xFFC7BCA1)
                Priority.LOW -> Color(0xFFF1D3B3)
                else -> Color(0xFFe6e1d5)
            },
            contentColor = Color.White,
            disabledContainerColor = Color.White,
            disabledContentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = RoundedCornerShape(5.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.completed,
                    onCheckedChange = { isChecked ->
                        onTaskCompleteToggle(task.copy(completed = isChecked))
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF65647C),
                        uncheckedColor = Color(0xFF65647C)
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Priority: ${task.priority}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Due Date: $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }

            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun TaskItemPreview() {
    val sampleTask = Task(
        id = "1",
        title = "Lorem ipsum",
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        priority = Priority.HIGH,
        completed = false,
    )

    TaskItem(
        task = sampleTask,
        onTaskCompleteToggle = {},
        onDeleteClick = { }
    )
}



