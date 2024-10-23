package com.example.todoapp.domain.intent

import com.example.todoapp.data.model.Task
//Intent osztály, amely az alkalmazás műveleteit összegyűjti, amelyekkel a felhasználó
//kapcsolatba léphet
sealed class TaskIntent {
    data class AddTask(val task: Task) : TaskIntent()
    object LoadTodos : TaskIntent()
}
