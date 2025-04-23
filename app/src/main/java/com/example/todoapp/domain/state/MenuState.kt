package com.example.todoapp.domain.state

import com.example.todoapp.domain.intent.MenuIntent

data class MenuState (
    val selectedItem: MenuIntent? = null
)