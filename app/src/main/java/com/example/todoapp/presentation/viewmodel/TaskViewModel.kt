package com.example.todoapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.firebase.FirebaseService
import com.example.todoapp.data.model.Task
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.domain.intent.TaskIntent
import com.example.todoapp.domain.state.TaskState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow<TaskState>(TaskState.Loading)
    val state: StateFlow<TaskState> = _state


    init {
        listenToTaskUpdates()
    }

    private fun listenToTaskUpdates() {
        taskRepository.listenToTasksUpdates { tasks ->
            viewModelScope.launch {
                val activeTasks = tasks.filter { !it.completed }
                val completedTasks = tasks.filter { it.completed }
                _state.value = TaskState.TasksLoaded(activeTasks, completedTasks)
                Log.d("TaskViewModel", "Snapshot listener updated tasks.")
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
        }
    }


    fun handleIntent(intent: TaskIntent) {
        viewModelScope.launch {
            Log.d("TaskViewModel", "Received intent: $intent")
            Log.d("TaskViewModel", "Handling intent: $intent")
            when (intent) {
                is TaskIntent.LoadTodos -> loadTasks()
                is TaskIntent.AddTask -> {
                    Log.d("TaskViewModel", "Handling AddTask intent for task: ${intent.task}")
                     addTask(intent.task)
                }
                is TaskIntent.UpdateTaskStatus -> updateTaskStatus(intent.taskId, intent.completed)
            }
        }
    }
    fun sendIntent(intent: TaskIntent) {
        handleIntent(intent)
    }

    private fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                val addedTask = taskRepository.addTask(task)
                if (addedTask != null) {
                    val currentState = _state.value
                    if (currentState is TaskState.TasksLoaded) {
                        val updatedActiveTasks = currentState.activeTasks.toMutableList()
                        updatedActiveTasks.add(addedTask)
                        _state.value = TaskState.TasksLoaded(
                            activeTasks = updatedActiveTasks,
                            completedTasks = currentState.completedTasks
                        )
                    }
                } else {
                    Log.e("TaskViewModel", "Task addition failed. Received null.")
                }
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error while adding task: ${e.message}")
            }
        }
    }


    private fun updateTaskStatus(taskId: String, completed: Boolean) {
        viewModelScope.launch {
            try {
                val success = taskRepository.updateTask(taskId, completed)
                if (success) {
                    loadTasks()
                } else {
                    _state.value = TaskState.Error("Failed to update task.")
                }
            } catch (e: Exception) {
                _state.value = TaskState.Error("Failed to update task: ${e.message}")
            }
        }
    }


    private fun loadTasks() {
        viewModelScope.launch {
            val tasks = taskRepository.getUserTasks()
            val completedTasks = tasks.filter { it.completed }
            val activeTasks = tasks.filter { !it.completed }
            Log.d("TaskViewModel", "Active tasks: $activeTasks")
            Log.d("TaskViewModel", "Completed tasks: $completedTasks")
            _state.value = TaskState.TasksLoaded(activeTasks, completedTasks)
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
            taskRepository.getUserId()
        } catch (e: Exception) {
            null
        }
    }
}
