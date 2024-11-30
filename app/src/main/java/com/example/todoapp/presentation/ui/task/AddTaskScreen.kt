package com.example.todoapp.presentation.ui.task

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todoapp.data.model.Priority
import com.example.todoapp.data.model.Task
import com.example.todoapp.domain.intent.TaskIntent
import com.example.todoapp.presentation.ui.theme.customTypography
import com.example.todoapp.presentation.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    onSaveTask: () -> Unit,
    viewModel: TaskViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.NONE) }
    var expanded by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val onSaveClick = {
        if (title.isNotBlank() && !isSaving) {
            isSaving = true
            val userId = viewModel.getCurrentUserId()
            if (userId != null) {
                val task = Task(
                    id = "",
                    title = title,
                    description = description,
                    dueDate = null,
                    priority = priority,
                    userId = userId,
                    completed = false
                )
                viewModel.sendIntent(TaskIntent.AddTask(task))
                onSaveTask()
            }
            isSaving = false
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Add new task",
            style = customTypography.titleLarge,
            fontSize = 30.sp,
            color = Color( 0xFF65647C)
            )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = title,
            onValueChange = { title = it },
            textStyle = customTypography.bodyMedium,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFD6D8DE),
                focusedContainerColor = Color(0xFFD6D8DE),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedLabelColor = Color.DarkGray,
                unfocusedLabelColor = Color.Gray,
                disabledLabelColor = Color.LightGray,
                cursorColor = Color.DarkGray),
            label = { Text("Title", style = customTypography.bodyMedium) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = description,
            onValueChange = { description = it },
            textStyle = customTypography.bodyMedium,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFD6D8DE),
                focusedContainerColor = Color(0xFFD6D8DE),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedLabelColor = Color.DarkGray,
                unfocusedLabelColor = Color.Gray,
                disabledLabelColor = Color.LightGray,
                cursorColor = Color.DarkGray),
            label = { Text("Description", style = customTypography.bodyMedium) },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))


        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = priority.name,
                onValueChange = {}, // Read-only field
                readOnly = true,
                textStyle = customTypography.bodyMedium,
                label = { Text("Priority", style = customTypography.bodyMedium) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFD6D8DE),
                    focusedContainerColor = Color(0xFFD6D8DE),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedLabelColor = Color.DarkGray,
                    unfocusedLabelColor = Color.Gray,
                    disabledLabelColor = Color.LightGray,
                    cursorColor = Color.DarkGray
                ),
                modifier = Modifier
                    .menuAnchor() // Aligns the menu with the text field
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("HIGH", style = customTypography.bodyMedium) },
                    onClick = {
                        priority = Priority.HIGH
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("MEDIUM", style = customTypography.bodyMedium) },
                    onClick = {
                        priority = Priority.MEDIUM
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("LOW", style = customTypography.bodyMedium) },
                    onClick = {
                        priority = Priority.LOW
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("NONE", style = customTypography.bodyMedium) },
                    onClick = {
                        priority = Priority.NONE
                        expanded = false
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(36.dp))
        FilledTonalButton(
            onClick = onSaveClick,
            enabled = !isSaving,
            colors = ButtonDefaults.buttonColors(containerColor = lightColorScheme().secondary),
        ) {
            Text("Add", style = customTypography.bodyLarge)
        }
    }
}
