package com.example.todoapp.presentation.ui.pomodoro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.minutes

@Composable
fun PomodoroTimerScreen() {
    var timeLeft by remember { mutableStateOf(30 * 60L) } // 30 perc (30 * 60 másodperc)
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        if (isRunning && timeLeft > 0) {
            while (timeLeft > 0 && isRunning) {
                delay(1000L) // Minden másodpercben csökkentjük az időt
                timeLeft -= 1
            }
            if (timeLeft == 0L) {
                isRunning = false // Ha az idő lejárt, állítsa le az időzítőt
            }
        }
    }

    val minutes = (timeLeft / 60).toString().padStart(2, '0')
    val seconds = (timeLeft % 60).toString().padStart(2, '0')

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0E0E0)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Visszaszámláló kijelzése
        Text(
            text = "$minutes:$seconds",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Indítás / Szünet gomb
        Button(onClick = {
            isRunning = !isRunning // Indítás vagy szünet
        }) {
            Text(if (isRunning) "Pause" else "Start")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visszaállítás gomb
        Button(onClick = {
            timeLeft = 30 * 60L // Idő visszaállítása 30 percre
            isRunning = false // Leállítás
        }) {
            Text("Reset")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PomodoroTimerPreview() {
    PomodoroTimerScreen()
}
