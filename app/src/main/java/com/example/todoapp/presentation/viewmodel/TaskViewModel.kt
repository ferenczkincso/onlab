package com.example.todoapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.model.Task
import com.example.todoapp.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    // StateFlow az aszinkron adatkezeléshez és figyeléshez
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        loadTasks()
    }

    // Taszkok betöltése az adatbázisból
    fun loadTasks() {
        viewModelScope.launch {
            val tasks = taskRepository.getUserTasks()
            tasks?.let {
                Log.d("TaskViewModel", "Loaded tasks: $it") // Log the loaded tasks
                _tasks.value = it // Update the state flow
            } ?: Log.e("TaskViewModel", "No tasks found or error occurred")
        }
    }


    // Új taszk hozzáadása
    fun addTask(task: Task) {
        viewModelScope.launch {
            taskRepository.addTask(task)
            loadTasks() // Frissítés a hozzáadás után
        }
    }

    // Taszk frissítése
    fun updateTask(taskId: String, task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(taskId, task)
            loadTasks() // Frissítés a módosítás után
        }
    }

    // Taszk törlése
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
            loadTasks() // Frissítés a törlés után
        }
    }

    fun toggleTaskCompletion(task: Task) {
        // Frissítse a feladat állapotát az adatbázisban
        // Majd frissítse a feladatokat a Firestore-ban
    }

}

