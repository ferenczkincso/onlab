package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.todoapp.data.firebase.FirebaseService
import com.example.todoapp.data.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val firebaseService: FirebaseService
) : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadTasks()
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    private fun loadTasks() {
        if (!firebaseService.isUserLoggedIn()) {
            _error.value = "You must be logged in to view tasks"
            return
        }

        firebaseService.listenToTasksUpdates { taskList ->
            _tasks.value = taskList
        }
    }

    fun clearError() {
        _error.value = null
    }
}
