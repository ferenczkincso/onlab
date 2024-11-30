package com.example.todoapp.data.firebase

import android.util.Log
import com.example.todoapp.data.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseService @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance() // FirebaseAuth példány

    private fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    // Bejelentkezés
    suspend fun login(email: String, password: String): Boolean {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user != null // Ellenőrizzük, hogy a bejelentkezett felhasználó nem null-e
        } catch (e: Exception) {
            // Hiba logolása
            Log.e("FirebaseService", "Login failed", e)
            false
        }
    }

    // Regisztráció
    suspend fun register(email: String, password: String): AuthResult? {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            // Hiba logolása
            Log.e("FirebaseService", "Registration failed", e)
            null
        }
    }

    // Felhasználó kijelentkezése
    fun logout() {
        try {
            FirebaseAuth.getInstance().signOut()
            // Ha van bármilyen lokális adat, itt törölheted azt is.
        } catch (e: Exception) {
            Log.e("FirebaseService", "Logout failed", e)
        }
    }

    // Ellenőrzi, hogy a felhasználó be van-e jelentkezve
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getUserTasksbyId(userId: String): Flow<List<Task>> {
        return callbackFlow {
            val tasksCollection = Firebase.firestore.collection("users")
                .document(userId)
                .collection("todos")

            tasksCollection.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val tasks = it.toObjects(Task::class.java)
                    trySend(tasks)
                }
            }
            awaitClose { }
        }
    }

    // Lekéri az autentikált felhasználó összes task-ját
    suspend fun getTasks(): List<Task>? {
        val userId = getUserId() ?: return null
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("todos")
                .get()
                .await()
            snapshot.toObjects(Task::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Failed to get tasks", e)
            null
        }
    }

    // Új task elmentése
    suspend fun addTask(task: Task): Boolean {
        val userId = getUserId() ?: return false
        val newTask = task.copy(userId = userId)
        return try {
            db.collection("users")
                .document(userId)
                .collection("todos")
                .add(newTask) // Itt newTask-ot használd
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Add task failed", e)
            false
        }
    }

    // Task törlése
    suspend fun deleteTask(taskId: String): Boolean {
        val userId = getUserId() ?: return false
        return try {
            db.collection("users")
                .document(userId)
                .collection("todos")
                .document(taskId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Delete task failed", e)
            false
        }
    }

    // Task frissítése
    suspend fun updateTask(taskId: String, updatedTask: Task): Boolean {
        val userId = getUserId() ?: return false
        return try {
            db.collection("users")
                .document(userId)
                .collection("todos")
                .document(taskId)
                .set(updatedTask)
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Update task failed", e)
            false
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // Feladatok lekérése Firestore-ból
    suspend fun getTasksFromFirestore(): QuerySnapshot {
        return try {
            Firebase.firestore.collection("todos").get().await() // Az összes task lekérdezése
        } catch (e: Exception) {
            Log.e("FirebaseService", "Failed to get tasks from Firestore", e)
            throw e
        }
    }

    // Feladat lekérése ID alapján Firestore-ból
    suspend fun getTaskByIdFromFirestore(taskId: String): DocumentSnapshot? {
        return try {
            Firebase.firestore.collection("todos")
                .document(taskId)
                .get()
                .await() // Egy adott task lekérése ID alapján
        } catch (e: Exception) {
            Log.e("FirebaseService", "Failed to get task by ID", e)
            null
        }
    }

    // Task frissítése Firestore-ban
    suspend fun updateTaskInFirestore(taskId: String, task: Task) {
        try {
            Firebase.firestore.collection("todos")
                .document(taskId)
                .set(task) // Feladat frissítése az ID alapján
                .await()
        } catch (e: Exception) {
            Log.e("FirebaseService", "Failed to update task in Firestore", e)
        }
    }
}

