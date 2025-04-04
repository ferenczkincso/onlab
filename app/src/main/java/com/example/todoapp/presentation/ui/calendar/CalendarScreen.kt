package com.example.todoapp.presentation.ui.calendar

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todoapp.data.firebase.FirebaseService
import com.example.todoapp.data.model.Task
import com.example.todoapp.presentation.viewmodel.CalendarViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel(),
    firebaseService: FirebaseService,
    onNavigateToAddTask: () -> Unit = {}
) {
    val tasks by viewModel.tasks.collectAsState(initial = emptyList())
    val selectedDate by viewModel.selectedDate.collectAsState()
    val error by viewModel.error.collectAsState()
    val currentDate = remember { LocalDate.now() }
    
    val datesWithTasks = remember(tasks) {
        tasks.mapNotNull { task -> 
            try {
                task.dueDate?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            } catch (e: Exception) {
                Log.e("CalendarScreen", "Error converting task date: ${e.message}")
                null
            }
        }.toSet()
    }
    
    // Calendar state
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val firstDayOfWeek = remember { DayOfWeek.MONDAY }
    
    // Calculate days in month
    val daysInMonth = remember(currentMonth) {
        val firstDay = currentMonth.atDay(1)
        val lastDay = currentMonth.atEndOfMonth()
        
        // Calculate the first day to show (including days from previous month)
        val firstDayOfCalendar = firstDay.minusDays(
            (firstDay.dayOfWeek.value - firstDayOfWeek.value + 7) % 7L
        )
        
        // Calculate the last day to show (including days from next month)
        val lastDayOfCalendar = lastDay.plusDays(
            (7 - lastDay.dayOfWeek.value + firstDayOfWeek.value - 1) % 7L
        )
        
        // Generate all days to display
        generateSequence(firstDayOfCalendar) { it.plusDays(1) }
            .takeWhile { it <= lastDayOfCalendar }
            .toList()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTask,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Error message
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.clearError() }) {
                            Text("×", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }

            // Calendar header with navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Text("←", style = MaterialTheme.typography.titleLarge)
                }
                Text(
                    text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Text("→", style = MaterialTheme.typography.titleLarge)
                }
            }
            
            // Calendar container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    // Weekday headers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                        daysOfWeek.forEach { day ->
                            Text(
                                text = day,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Calendar grid
                    Column(modifier = Modifier.fillMaxWidth()) {
                        daysInMonth.chunked(7).forEach { week ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                week.forEach { date ->
                                    Day(
                                        date = date,
                                        isCurrentMonth = date.month == currentMonth.month,
                                        isSelected = date == (selectedDate ?: currentDate),
                                        isToday = date == currentDate,
                                        hasTask = datesWithTasks.contains(date),
                                        onDateSelected = { viewModel.setSelectedDate(date) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            selectedDate?.let { date ->
                val tasksForSelectedDate = tasks.filter { task ->
                    try {
                        task.dueDate?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate() == date
                    } catch (e: Exception) {
                        false
                    }
                }
                
                if (tasksForSelectedDate.isNotEmpty()) {
                    Text(
                        text = "Tasks for ${date.format(java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy"))}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tasksForSelectedDate) { task ->
                            TaskCard(task = task)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Day(
    date: LocalDate,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    hasTask: Boolean,
    onDateSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .padding(2.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.primaryContainer
                    else -> Color.Transparent
                }
            )
            .border(
                width = if (hasTask) 2.dp else 0.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .clickable(onClick = onDateSelected),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                !isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
private fun TaskCard(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.priority.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = when (task.priority) {
                        com.example.todoapp.data.model.Priority.HIGH -> MaterialTheme.colorScheme.error
                        com.example.todoapp.data.model.Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary
                        com.example.todoapp.data.model.Priority.LOW -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
                
                if (task.completed) {
                    Text(
                        text = "Completed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
