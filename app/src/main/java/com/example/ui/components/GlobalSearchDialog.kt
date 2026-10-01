package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*

@Composable
fun GlobalSearchDialog(
    tasks: List<TaskEntity>,
    notes: List<NoteEntity>,
    projects: List<ProjectEntity>,
    goals: List<GoalEntity>,
    events: List<CalendarEventEntity>,
    habits: List<HabitEntity>,
    studySubjects: List<StudySubjectEntity>,
    onDismiss: () -> Unit,
    onSelectItem: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val q = query.trim().lowercase()

    val matchingTasks = remember(q, tasks) {
        if (q.isEmpty()) emptyList() else tasks.filter { it.title.lowercase().contains(q) || it.tags.lowercase().contains(q) || it.description.lowercase().contains(q) }
    }
    val matchingNotes = remember(q, notes) {
        if (q.isEmpty()) emptyList() else notes.filter { it.title.lowercase().contains(q) || it.content.lowercase().contains(q) || it.tags.lowercase().contains(q) }
    }
    val matchingProjects = remember(q, projects) {
        if (q.isEmpty()) emptyList() else projects.filter { it.name.lowercase().contains(q) || it.description.lowercase().contains(q) }
    }
    val matchingGoals = remember(q, goals) {
        if (q.isEmpty()) emptyList() else goals.filter { it.title.lowercase().contains(q) || it.description.lowercase().contains(q) }
    }
    val matchingEvents = remember(q, events) {
        if (q.isEmpty()) emptyList() else events.filter { it.title.lowercase().contains(q) || it.notes.lowercase().contains(q) }
    }
    val matchingHabits = remember(q, habits) {
        if (q.isEmpty()) emptyList() else habits.filter { it.name.lowercase().contains(q) }
    }
    val matchingSubjects = remember(q, studySubjects) {
        if (q.isEmpty()) emptyList() else studySubjects.filter { it.name.lowercase().contains(q) }
    }

    val totalMatches = matchingTasks.size + matchingNotes.size + matchingProjects.size + matchingGoals.size + matchingEvents.size + matchingHabits.size + matchingSubjects.size

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Global Search",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close search")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search tasks, notes, goals, study...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("global_search_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (query.isNotBlank()) {
                    Text(
                        text = "$totalMatches result(s) found across LifeOS",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (query.isBlank()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    "Type anything to search across all your life systems...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (matchingTasks.isNotEmpty()) {
                        item { Text("Tasks (${matchingTasks.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall) }
                        items(matchingTasks) { task ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.fillMaxWidth().clickable { onSelectItem("Task: ${task.title}") }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(task.title, fontWeight = FontWeight.SemiBold)
                                    if (task.tags.isNotBlank()) {
                                        Text("Tags: ${task.tags}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }

                    if (matchingProjects.isNotEmpty()) {
                        item { Text("Projects (${matchingProjects.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall) }
                        items(matchingProjects) { proj ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.fillMaxWidth().clickable { onSelectItem("Project: ${proj.name}") }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(proj.name, fontWeight = FontWeight.SemiBold)
                                    Text(proj.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    if (matchingNotes.isNotEmpty()) {
                        item { Text("Notes (${matchingNotes.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall) }
                        items(matchingNotes) { note ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.fillMaxWidth().clickable { onSelectItem("Note: ${note.title}") }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(note.title, fontWeight = FontWeight.SemiBold)
                                    Text(note.content.take(80) + "...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    if (matchingGoals.isNotEmpty()) {
                        item { Text("Goals (${matchingGoals.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall) }
                        items(matchingGoals) { goal ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.fillMaxWidth().clickable { onSelectItem("Goal: ${goal.title}") }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(goal.title, fontWeight = FontWeight.SemiBold)
                                    Text("Progress: ${goal.currentValue.toInt()}%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }

                    if (matchingEvents.isNotEmpty()) {
                        item { Text("Calendar Events (${matchingEvents.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall) }
                        items(matchingEvents) { evt ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.fillMaxWidth().clickable { onSelectItem("Event: ${evt.title}") }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(evt.title, fontWeight = FontWeight.SemiBold)
                                    Text(evt.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
