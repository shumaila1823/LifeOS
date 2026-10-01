package com.example.ui.screens.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Priority
import com.example.data.model.ProjectEntity
import com.example.data.model.ProjectHealth
import com.example.data.model.TaskEntity
import com.example.ui.components.CreateTaskDialog
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseCritical
import com.example.ui.viewmodel.TaskFilterType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TasksScreen(
    tasks: List<TaskEntity>,
    projects: List<ProjectEntity>,
    filter: TaskFilterType,
    onFilterChange: (TaskFilterType) -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onCreateTask: (TaskEntity) -> Unit,
    onCreateProject: (ProjectEntity) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Tasks, 1: Projects
    var showCreateTaskDialog by remember { mutableStateOf(false) }
    var showCreateProjectDialog by remember { mutableStateOf(false) }
    var expandedTaskId by remember { mutableStateOf<String?>(null) }

    val now = System.currentTimeMillis()
    val filteredTasks = remember(tasks, filter) {
        when (filter) {
            TaskFilterType.ALL -> tasks
            TaskFilterType.TODAY -> tasks.filter { !it.isCompleted && (it.deadlineEpochMs == null || it.deadlineEpochMs - now < 24 * 3600 * 1000) }
            TaskFilterType.UPCOMING -> tasks.filter { !it.isCompleted && (it.deadlineEpochMs != null && it.deadlineEpochMs - now >= 24 * 3600 * 1000) }
            TaskFilterType.OVERDUE -> tasks.filter { !it.isCompleted && it.deadlineEpochMs != null && it.deadlineEpochMs < now }
            TaskFilterType.COMPLETED -> tasks.filter { it.isCompleted }
            TaskFilterType.HIGH_PRIORITY -> tasks.filter { !it.isCompleted && (it.priority == Priority.HIGH || it.priority == Priority.CRITICAL) }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showCreateTaskDialog = true else showCreateProjectDialog = true
                },
                modifier = Modifier.testTag("add_task_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("tasks_screen")
        ) {
            // Screen Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = if (selectedTab == 0) "Task Management" else "Projects & Initiatives",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            // Tab Row (Tasks vs Projects)
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Tasks (${tasks.count { !it.isCompleted }})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Projects (${projects.size})") }
                )
            }

            if (selectedTab == 0) {
                // Task Filter Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(TaskFilterType.values()) { f ->
                        FilterChip(
                            selected = filter == f,
                            onClick = { onFilterChange(f) },
                            label = { Text(f.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }

                if (filteredTasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "No tasks in this filter.",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Tap '+' below to add a new task or ask LifeOS to schedule one.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredTasks, key = { it.id }) { task ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth().testTag("task_item_${task.id}")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Checkbox(
                                            checked = task.isCompleted,
                                            onCheckedChange = { onToggleTask(task) }
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = task.title,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.SemiBold,
                                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                            )
                                            if (task.description.isNotBlank()) {
                                                Text(
                                                    text = task.description,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 2
                                                )
                                            }
                                        }
                                        PriorityBadge(task.priority)
                                        IconButton(onClick = { onDeleteTask(task) }) {
                                            Icon(
                                                Icons.Default.DeleteOutline,
                                                contentDescription = "Delete task",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }

                                    // Tags & metadata row
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(start = 48.dp, top = 4.dp)
                                    ) {
                                        if (task.deadlineEpochMs != null) {
                                            val deadlineStr = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(task.deadlineEpochMs))
                                            Text(
                                                "📅 Due $deadlineStr",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Text(
                                            "⏱️ ${task.estimatedMinutes}m",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (task.tags.isNotBlank()) {
                                            Text(
                                                "🏷️ ${task.tags}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    // Why Recommended Explainer
                                    if (task.priorityReason.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .padding(start = 48.dp)
                                                .clickable {
                                                    expandedTaskId = if (expandedTaskId == task.id) null else task.id
                                                }
                                        ) {
                                            Text(
                                                "Why this priority?",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Icon(
                                                if (expandedTaskId == task.id) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        AnimatedVisibility(visible = expandedTaskId == task.id) {
                                            Card(
                                                shape = RoundedCornerShape(8.dp),
                                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(start = 48.dp, top = 4.dp)
                                            ) {
                                                Text(
                                                    text = task.priorityReason,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.padding(8.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Projects Tab
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(projects) { project ->
                        val healthColor = when (project.health) {
                            ProjectHealth.ON_TRACK -> EmeraldSuccess
                            ProjectHealth.AT_RISK -> Color(0xFFF59E0B)
                            ProjectHealth.BEHIND -> RoseCritical
                        }
                        val linkedTasks = tasks.filter { it.projectId == project.id }
                        val completedCount = linkedTasks.count { it.isCompleted }
                        val totalCount = linkedTasks.size
                        val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0.5f

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = project.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = project.health.name.replace("_", " "),
                                        color = healthColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .background(healthColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }

                                if (project.description.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = project.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        "Progress (${(progress * 100).toInt()}%)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        "$completedCount of $totalCount tasks done",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { progress },
                                    color = healthColor,
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Status: ${project.healthReason}",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateTaskDialog) {
        CreateTaskDialog(
            onDismiss = { showCreateTaskDialog = false },
            onSave = {
                onCreateTask(it)
                showCreateTaskDialog = false
            }
        )
    }

    if (showCreateProjectDialog) {
        var projName by remember { mutableStateOf("") }
        var projDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateProjectDialog = false },
            title = { Text("New Project") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = projName,
                        onValueChange = { projName = it },
                        label = { Text("Project Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = projDesc,
                        onValueChange = { projDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (projName.isNotBlank()) {
                            onCreateProject(
                                ProjectEntity(
                                    name = projName.trim(),
                                    description = projDesc.trim(),
                                    health = ProjectHealth.ON_TRACK,
                                    healthReason = "Initial planning phase"
                                )
                            )
                            showCreateProjectDialog = false
                        }
                    },
                    enabled = projName.isNotBlank()
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateProjectDialog = false }) { Text("Cancel") }
            }
        )
    }
}
