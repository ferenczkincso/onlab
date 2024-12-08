package com.example.todoapp

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.firestore.firestore
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp

//Erre a fájlra azért van szükség külön, hogy a Hiltet tudjuk használni.
//Az AndroidManifest.xml fájlban ez van megadva a android::name-nek
//Az alkalmazás egész életciklusát kezeli (Application)
@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
    }
}