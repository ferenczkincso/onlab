package com.example.todoapp.domain.intent

sealed class MenuIntent {
    object TaskListClicked : MenuIntent()
    object PomodoroClicked : MenuIntent()
    object DoneTasksClicked : MenuIntent()
    object CalendarClicked : MenuIntent()
}