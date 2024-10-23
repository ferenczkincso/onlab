package com.example.todoapp.data.repository
import android.util.Log
import com.example.todoapp.data.firebase.FirebaseService
import com.example.todoapp.data.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

//FirebaseService függvényeit hívja meg minden függvény
//Az MVI architektúra átláthatósága miatt lett megtartva
//Csupán egy közvetítő réteg, ami az üzleti logikát egyszerűsíti, és elrejti a Firestore implementációt a ViewModel elől

import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val firebaseService: FirebaseService
) {

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



    suspend fun addTask(task: Task) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            // Új feladat létrehozása a bejelentkezett felhasználó UID-jával
            val newTask = task.copy(userId = userId)  // a Task példányhoz hozzáadjuk a userId-t

            // Feladat mentése a Firestore-ba a "users" gyűjteményen belül
            Firebase.firestore.collection("users")
                .document(userId)
                .collection("todos")  // itt a felhasználó saját feladatai tárolódnak
                .add(newTask)
                .addOnSuccessListener {
                    Log.d("TaskRepository", "Task successfully added!")
                }
                .addOnFailureListener { e ->
                    Log.w("TaskRepository", "Error adding task", e)
                }
        } else {
            Log.w("TaskRepository", "No authenticated user.")
        }
    }


    suspend fun updateTask(taskId: String, updatedTask: Task): Boolean {
        return firebaseService.updateTask(taskId, updatedTask)
    }

    suspend fun deleteTask(taskId: String): Boolean {
        return firebaseService.deleteTask(taskId)
    }
}
