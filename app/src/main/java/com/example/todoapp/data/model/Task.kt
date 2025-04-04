package com.example.todoapp.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Task(
    val id: String = "",

    @PropertyName("title")
    val title: String = "",

    @PropertyName("description")
    val description: String = "",

    @PropertyName("priority")
    val priority: Priority = Priority.NONE,

    @PropertyName("dueDate")
    val dueDate: Timestamp? = null,

    @PropertyName("userId")
    val userId: String = "",

    @PropertyName("completed")
    val completed: Boolean = false
)
