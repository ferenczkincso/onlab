package com.example.todoapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.model.Task
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.domain.intent.TaskIntent
import com.example.todoapp.domain.state.TaskState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    // Az állapot tárolása StateFlow-val
    private val _state = MutableStateFlow<TaskState>(TaskState.Loading)
    val state: StateFlow<TaskState> = _state

    // Intentek kezelése
    private val _taskIntent = MutableSharedFlow<TaskIntent>()
    private val taskIntent = _taskIntent.asSharedFlow()

    init {
        loadTasks()
    }

//    private fun loadTasks() {
//        viewModelScope.launch {
//            val tasks = taskRepository.getTasks() // Ellenőrizd, hogy itt 9 taszk jön vissza
//            _state.value = if (tasks.isNotEmpty()) {
//                TaskState.TasksLoaded(tasks)
//            } else {
//                TaskState.Error("No tasks found")
//            }
//        }
//    }

    fun sendIntent(intent: TaskIntent) {
        viewModelScope.launch {
            _taskIntent.emit(intent)
        }
    }

    private fun handleIntents() {
        viewModelScope.launch {
            taskIntent.collectLatest { intent ->
                when (intent) {
                    is TaskIntent.LoadTodos -> loadTasks()
                    is TaskIntent.AddTask -> addTask(intent.task)
                    is TaskIntent.UpdateTaskStatus -> updateTaskStatus(intent.taskId, intent.completed)
                    is TaskIntent.ShowCompletedTasks -> filterCompletedTasks()
                    TaskIntent.LoadCompletedTodos -> loadCompletedTasks()
                }
            }
        }
    }

    fun loadTasks() {
        viewModelScope.launch {
            val tasks = taskRepository.getUserTasks()
            val completedTasks = tasks?.filter { it.completed } ?: emptyList()  // Befejezett feladatok
            val activeTasks = tasks?.filter { !it.completed } ?: emptyList()  // Aktív feladatok
            _state.value = TaskState.TasksLoaded(activeTasks, completedTasks)  // Mindkettőt betöltjük
        }
    }




    fun loadCompletedTasks() {
        viewModelScope.launch {
            _state.value = TaskState.Loading
            try {
                val tasks = taskRepository.getUserTasks()?.filter { it.completed } ?: emptyList()
                _state.value = TaskState.TasksLoaded(
                    activeTasks = emptyList(),
                    completedTasks = tasks
                )
            } catch (e: Exception) {
                _state.value = TaskState.Error("Failed to load completed tasks: ${e.message}")
            }
        }
    }

    private fun addTask(task: Task) {
        viewModelScope.launch {
            _state.value = TaskState.Loading
            try {
                val success = taskRepository.addTask(task)
                if (success) {
                    sendIntent(TaskIntent.LoadTodos) // Frissítés
                } else {
                    _state.value = TaskState.Error("Failed to add task")
                }
            } catch (e: Exception) {
                _state.value = TaskState.Error("Error adding task: ${e.message}")
            }
        }
    }

    private fun updateTaskStatus(taskId: String, completed: Boolean) {
        viewModelScope.launch {
            _state.value = TaskState.Loading
            try {
                val taskToUpdate = taskRepository.getTaskById(taskId) ?: return@launch
                val updatedTask = taskToUpdate.copy(completed = completed)
                val success = taskRepository.updateTask(taskId, updatedTask)

                if (success) {
                    sendIntent(TaskIntent.LoadTodos)
                } else {
                    _state.value = TaskState.Error("Failed to update task status")
                }
            } catch (e: Exception) {
                _state.value = TaskState.Error("Error updating task status: ${e.message}")
            }
        }
    }

    private fun filterCompletedTasks() {
        viewModelScope.launch {
            val currentState = _state.value
            if (currentState is TaskState.TasksLoaded) {
                _state.value = TaskState.TasksLoaded(
                    activeTasks = emptyList(),
                    completedTasks = currentState.completedTasks
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _state.value = TaskState.Loading
            try {
                taskRepository.firebaseService.logout()
                _state.value = TaskState.UserLoggedOut
            } catch (e: Exception) {
                _state.value = TaskState.Error("Failed to log out: ${e.message}")
            }
        }
    }

    fun getCurrentUserId(): String? {
        return try {
            taskRepository.getUserId() // Ha nincs bejelentkezve, akkor null-t ad vissza.
        } catch (e: Exception) {
            null
        }
    }
}
