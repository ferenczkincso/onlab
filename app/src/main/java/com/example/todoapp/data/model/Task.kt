package com.example.todoapp.data.model

import com.google.firebase.Timestamp

data class Task(
    val id:String ="",
    val title: String = "",
    val description: String = "",
    val dueDate: Timestamp? = null, // Itt a dueDate is Timestamp típus
    val priority: Priority = Priority.NONE,
    val userId: String = "",
    val isCompleted: Boolean = false
)