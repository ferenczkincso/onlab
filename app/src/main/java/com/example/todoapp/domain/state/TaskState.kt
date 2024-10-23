package com.example.todoapp.domain.state

import com.example.todoapp.data.model.Task

sealed class TaskState {
    object Idle : TaskState()
    object Loading : TaskState()
    data class TasksLoaded(val tasks: List<Task>) : TaskState()
    data class Error(val message: String) : TaskState()
}
