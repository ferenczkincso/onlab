package com.example.todoapp.presentation.ui.task

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todoapp.data.firebase.FirebaseService
import com.example.todoapp.data.model.Priority
import com.example.todoapp.data.model.Task
import com.example.todoapp.domain.intent.TaskIntent
import com.example.todoapp.presentation.ui.theme.customTypography
import com.example.todoapp.presentation.viewmodel.TaskViewModel
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.Date
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

fun convertMillisToTimestamp(millis: Long?): Timestamp? {
    return millis?.let { Timestamp(Date(it)) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    onSaveTask: () -> Unit,
    viewModel: TaskViewModel = hiltViewModel(),
    firebaseService: FirebaseService
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.NONE) }
    var expanded by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedDate by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    if (!firebaseService.isUserLoggedIn()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "You must be logged in to add tasks",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onSaveTask) {
                Text("Go Back")
            }
        }
        return
    }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, day, 0, 0, 0)
            selectedDateMillis = calendar.timeInMillis
            selectedDate = "$year-${month + 1}-$day"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val onSaveClick = {
        if (title.isNotBlank() && !isSaving) {
            isSaving = true
            val task = Task(
                title = title,
                description = description,
                dueDate = convertMillisToTimestamp(selectedDateMillis),
                priority = priority,
                userId = firebaseService.getUserId() ?: "",
                completed = false
            )
            
            viewModel.addTask(task)
            Log.d("TaskScreen", "Intent sent for addition")
            isSaving = false
            onSaveTask()
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
                onValueChange = {},
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
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = selectedDate,
            onValueChange = {},
            readOnly = true,
            textStyle = customTypography.bodyMedium,
            label = { Text("Due Date",style = customTypography.bodyMedium) },
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Pick Date"
                    )
                }
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
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(36.dp))
        FilledTonalButton(
            onClick ={
                Log.d("TaskScreen", "Add Task button clicked with name: $title, description: $description")
                onSaveClick()
            },
            enabled = !isSaving,
            colors = ButtonDefaults.buttonColors(containerColor = lightColorScheme().secondary),
        ) {
            Text("Add", style = customTypography.bodyLarge)
        }
    }
}
