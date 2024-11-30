package com.example.todoapp.data.repository
import android.util.Log
import com.example.todoapp.data.firebase.FirebaseService
import com.example.todoapp.data.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

//FirebaseService függvényeit hívja meg minden függvény
//Az MVI architektúra átláthatósága miatt lett megtartva
//Csupán egy közvetítő réteg, ami az üzleti logikát egyszerűsíti, és elrejti a Firestore implementációt a ViewModel elől

import javax.inject.Inject

class TaskRepository @Inject constructor(
    val firebaseService: FirebaseService
) {
    fun getUserId(): String? {
        return firebaseService.getCurrentUserId()
    }

    suspend fun getUserTasks(): List<Task>? {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return null // Ellenőrizd, hogy a felhasználó hitelesítve van-e
        Log.d("TaskRepository", "Feladatok betöltése a következő felhasználóhoz: $userId")

        return try {
            val tasks = mutableListOf<Task>()
            val snapshot = Firebase.firestore.collection("users")
                .document(userId) // A konkrét felhasználó dokumentuma
                .collection("todos") // A todos alkategória
                .get()
                .await() // Várj a lekérdezés befejeződésére

            Log.d("TaskRepository", "Betöltött feladatok száma: ${snapshot.size()}") // Naplózd a betöltött feladatok számát

            for (document in snapshot.documents) {
                val task = document.toObject(Task::class.java) // Hozd létre a Task objektumot a dokumentumból
                task?.let { tasks.add(it) } // Add a listához, ha nem null
            }
            tasks // Térj vissza a feladatok listájával
        } catch (e: Exception) {
            Log.e("TaskRepository", "Hiba a feladatok lekérdezésekor", e)
            null // Visszatérés null, ha hiba történt
        }
    }


    suspend fun addTask(task: Task): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser

        return if (currentUser != null) {
            val userId = currentUser.uid

            // Új feladat létrehozása a bejelentkezett felhasználó UID-jával
            val newTask = task.copy(userId = userId)

            try {
                // Feladat mentése Firestore-ba, várakozással
                Firebase.firestore.collection("users")
                    .document(userId)
                    .collection("todos")
                    .add(newTask)
                    .await()  // Várunk, amíg a művelet befejeződik

                Log.d("TaskRepository", "Task successfully added!")
                true  // Sikeres mentés esetén true érték
            } catch (e: Exception) {
                Log.w("TaskRepository", "Error adding task", e)
                false  // Sikertelen mentés esetén false érték
            }
        } else {
            Log.w("TaskRepository", "No authenticated user.")
            false
        }
    }

    suspend fun updateTask(taskId: String, updatedTask: Task): Boolean {
        return firebaseService.updateTask(taskId, updatedTask)
    }

    suspend fun deleteTask(taskId: String): Boolean {
        return firebaseService.deleteTask(taskId)
    }

    fun getCompletedTasksFlow(): Flow<List<Task>> = callbackFlow {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val listener = Firebase.firestore.collection("users")
                .document(userId)
                .collection("todos")
                .whereEqualTo("completed", true)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val tasks = snapshot?.documents?.mapNotNull { it.toObject(Task::class.java) } ?: emptyList()
                    trySend(tasks)
                }

            awaitClose { listener.remove() }
        } else {
            close(Exception("No authenticated user."))
        }
    }


    suspend fun getCompletedTasks(): List<Task> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return emptyList()
        return try {
            val snapshot = Firebase.firestore.collection("users")
                .document(userId)
                .collection("todos")
                .whereEqualTo("completed", true)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error fetching completed tasks", e)
            emptyList()
        }
    }

    suspend fun markTaskAsCompleted(taskId: String): Boolean {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return false
        return try {
            Firebase.firestore.collection("users")
                .document(userId)
                .collection("todos")
                .document(taskId)
                .update("completed", true)
                .await()

            Log.d("TaskRepository", "Task marked as completed!")
            true
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error marking task as completed", e)
            false
        }
    }

    suspend fun getTaskById(taskId: String): Task? {
        return try {
            val taskDocument = firebaseService.getTaskByIdFromFirestore(taskId) // Lekérdezés ID alapján
            taskDocument?.toObject(Task::class.java) // Átalakítjuk a Firestore dokumentumot Task objektummá
        } catch (e: Exception) {
            null
        }
    }

}
