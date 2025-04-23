package com.example.todoapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.todoapp.domain.intent.MenuIntent
import com.example.todoapp.domain.state.MenuState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MenuViewModel : ViewModel() {
    private val _state = MutableStateFlow(MenuState())
    val state: StateFlow<MenuState> = _state.asStateFlow()

    fun processIntent(intent: MenuIntent) {
        _state.value = _state.value.copy(selectedItem = intent)
    }
}
