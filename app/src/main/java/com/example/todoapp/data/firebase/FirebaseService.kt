package com.example.todoapp.data.firebase

import android.util.Log
import com.example.todoapp.data.model.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseService @Inject constructor(
    private val db: FirebaseFirestore,
    val auth: FirebaseAuth

) {


    fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    private fun userTasksCollection(): CollectionReference? {
        val userId = getUserId()
        Log.d("FirebaseService", "Current User ID: $userId")
        return if (userId != null) {
            db.collection("users").document(userId).collection("todos")
        } else {
            null
        }
    }

    private fun logError(tag: String, message: String, exception: Exception) {
        Log.e(tag, message, exception)
    }

    fun logout() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            logError("FirebaseService", "Logout failed", e)
        }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getUserTasksById(userId: String): Flow<List<Task>> = callbackFlow {
        val tasksCollection = db.collection("users").document(userId).collection("todos")
        val listener = tasksCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                close(exception)
                return@addSnapshotListener
            }

            val tasks = snapshot?.documents?.mapNotNull { doc ->
                val task = doc.toObject(Task::class.java) ?: return@mapNotNull null
                task.copy(id = doc.id)
            } ?: emptyList()

            trySend(tasks).isSuccess
        }
        awaitClose { listener.remove() }
    }



    fun addTask(task: Task, callback: (Task?) -> Unit) {
        val tasksCollection = userTasksCollection()
        if (tasksCollection == null) {
            Log.e("FirebaseService", "Tasks collection is null. User not logged in.")
            callback(null)
            return
        }

        tasksCollection.add(task)
            .addOnSuccessListener { documentReference ->
                val taskWithId = task.copy(id = documentReference.id) // ID hozzárendelése
                Log.d("FirebaseService", "Document added successfully with ID: ${documentReference.id}")
                callback(taskWithId)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseService", "Error adding document to Firestore", e) // Hiba logolása
                callback(null)
            }
    }

    fun listenToTasksUpdates(onTasksChanged: (List<Task>) -> Unit) {
        val tasksCollection = userTasksCollection() ?: return
        tasksCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e("FirebaseService", "Snapshot listener error", exception)
                return@addSnapshotListener
            }
            val tasks = snapshot?.documents?.mapNotNull { doc ->
                val task = doc.toObject(Task::class.java)
                task?.copy(id = doc.id)
            } ?: emptyList()

            Log.d("FirebaseService", "Tasks updated: $tasks")
            onTasksChanged(tasks)
        }
    }
    suspend fun deleteTask(taskId: String): Boolean {
        val tasksCollection = userTasksCollection() ?: return false
        return try {
            tasksCollection.document(taskId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error deleting task", e)
            false
        }
    }
    suspend fun updateTask(taskDocumentId: String, completed: Boolean): Boolean {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return false
        return try {
            db.collection("users")
                .document(userId)
                .collection("todos")
                .document(taskDocumentId)
                .update("completed", completed)
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error updating task", e)
            false
        }
    }
}
