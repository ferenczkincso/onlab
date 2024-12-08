package com.example.todoapp.data.repository
import android.content.ContentValues.TAG
import android.util.Log
import com.example.todoapp.data.firebase.FirebaseService
import com.example.todoapp.data.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


//FirebaseService függvényeit hívja meg minden függvény
//Az MVI architektúra átláthatósága miatt lett megtartva
//Csupán egy közvetítő réteg, ami az üzleti logikát egyszerűsíti, és elrejti a Firestore implementációt a ViewModel elől


class TaskRepository @Inject constructor(
    val firebaseService: FirebaseService
) {
    fun getUserId(): String? {
        return firebaseService.getUserId()
    }

    suspend fun getUserTasks(): List<Task> {
        val tasks = firebaseService.getUserTasksById(firebaseService.getUserId() ?: "").firstOrNull() ?: emptyList()
        Log.d("TaskRepository", "Fetched tasks: $tasks")
        return tasks
    }

    suspend fun addTask(task: Task): Task? {
        Log.d("TaskRepository", "Starting addTask for: $task") // Task mentés indítása
        return suspendCoroutine { continuation ->
            firebaseService.addTask(task) { taskWithId ->
                if (taskWithId != null) {
                    Log.d("TaskRepository", "Task added successfully: $taskWithId")
                    continuation.resume(taskWithId) // Sikeres mentés
                } else {
                    Log.e("TaskRepository", "Failed to add task in FirebaseService")
                    continuation.resumeWithException(Exception("Failed to add task")) // Hiba
                }
            }
        }
    }
    suspend fun updateTask(taskId: String, completed: Boolean): Boolean {
            return firebaseService.updateTask(taskId, completed)
        }

    suspend fun deleteTask(taskId: String): Boolean {
        return firebaseService.deleteTask(taskId)
    }


    fun listenToTasksUpdates(onTasksChanged: (List<Task>) -> Unit) {
        firebaseService.listenToTasksUpdates(onTasksChanged)
    }
}
