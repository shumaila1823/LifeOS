package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class LifeOsScreen(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    Dashboard("Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    Tasks("Tasks", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle),
    Calendar("Calendar", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    HabitsGoals("Habits & Goals", Icons.Filled.Flag, Icons.Outlined.Flag),
    StudyNotes("Study & Notes", Icons.Filled.School, Icons.Outlined.School),
    AiAssistant("AI Assistant", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    Settings("Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}
