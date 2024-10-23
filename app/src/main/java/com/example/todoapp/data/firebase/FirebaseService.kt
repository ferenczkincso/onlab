package com.example.todoapp.data.firebase

import com.example.todoapp.data.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
            false
        }
    }

    // Regisztráció
    suspend fun register(email: String, password: String): AuthResult? {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            null
        }
    }

    // Felhasználó kijelentkezése
    fun logout() {
        auth.signOut() // Firebase kijelentkezés
    }

    // Ellenőrzi, hogy a felhasználó be van-e jelentkezve
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    suspend fun getTasksByUserId(userId: String): List<Task> {
        val tasks = mutableListOf<Task>()

        // Firestore lekérdezés, ahol a 'userId' mező az aktuális felhasználó UID-ja
        val querySnapshot = Firebase.firestore.collection("tasks")
            .whereEqualTo("userId", userId)  // Csak a felhasználóhoz tartozó teendőket kérdezi le
            .get()
            .await()  // Kotlin koroutine támogatás

        for (document in querySnapshot.documents) {
            document.toObject(Task::class.java)?.let { task ->
                tasks.add(task)
            }
        }

        return tasks
    }


    // Lekéri az autentikált felhasználó összes task-ját
    suspend fun getTasks(): List<Task>? {
        val userId = getUserId() ?: return null
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("tasks")
                .get()
                .await()
            snapshot.toObjects(Task::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // Új task elmentése
    suspend fun addTask(task: Task): Boolean {
        val userId = getUserId() ?: return false
        val newTask = task.copy(userId = userId)
        return try {
            db.collection("users")
                .document(userId) // A dokumentum azonosítója a felhasználó UID-ja
                .collection("tasks")
                .add(task)
            true
        } catch (e: Exception) {
            false
        }
    }

    // Task törlése
    suspend fun deleteTask(taskId: String): Boolean {
        val userId = getUserId() ?: return false
        return try {
            db.collection("users")
                .document(userId)
                .collection("tasks")
                .document(taskId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Task frissítése
    suspend fun updateTask(taskId: String, updatedTask: Task): Boolean {
        val userId = getUserId() ?: return false
        return try {
            db.collection("users")
                .document(userId)
                .collection("tasks")
                .document(taskId)
                .set(updatedTask)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
