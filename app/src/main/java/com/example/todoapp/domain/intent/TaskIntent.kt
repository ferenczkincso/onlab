package com.example.todoapp.domain.intent

import com.example.todoapp.data.model.Task
//Intent osztály, amely az alkalmazás műveleteit összegyűjti, amelyekkel a felhasználó
//kapcsolatba léphet

sealed class TaskIntent {
    object LoadTodos : TaskIntent()
    data class UpdateTaskStatus(val taskId: String, val completed: Boolean) : TaskIntent()
    data class AddTask(val task: Task) : TaskIntent()  // taskId hozzáadása
    object ShowCompletedTasks : TaskIntent()
    object LoadCompletedTodos : TaskIntent()
    data class MarkTaskAsCompleted(val taskId: String) : TaskIntent()
}

