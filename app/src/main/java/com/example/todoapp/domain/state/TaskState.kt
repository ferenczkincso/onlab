package com.example.todoapp.domain.state

import com.example.todoapp.data.model.Task

sealed class TaskState {
    object Loading : TaskState()
    data class TasksLoaded(val activeTasks: List<Task>, val completedTasks: List<Task>) : TaskState()
    data class Error(val message: String) : TaskState()
    object UserLoggedOut : TaskState()
}

