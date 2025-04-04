package com.example.todoapp.presentation.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.firebase.FirebaseService
import com.example.todoapp.data.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val firebaseService: FirebaseService
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        if (!firebaseService.isUserLoggedIn()) {
            _error.value = "You must be logged in to view tasks"
            return
        }

        firebaseService.listenToTasksUpdates { taskList ->
            _tasks.value = taskList
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                firebaseService.addTask(task) { addedTask ->
                    if (addedTask == null) {
                        _error.value = "Failed to add task"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error adding task: ${e.message}"
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                // Note: The FirebaseService only supports updating the completed status
                // We'll need to modify it to support full task updates if needed
                val success = firebaseService.updateTask(task.id, task.completed)
                if (!success) {
                    _error.value = "Failed to update task"
                }
            } catch (e: Exception) {
                _error.value = "Error updating task: ${e.message}"
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                val success = firebaseService.deleteTask(taskId)
                if (!success) {
                    _error.value = "Failed to delete task"
                }
            } catch (e: Exception) {
                _error.value = "Error deleting task: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
} 