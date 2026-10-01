package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun OnboardingDialog(
    onDismiss: () -> Unit,
    onComplete: (goals: String, hours: String, focusArea: String, notificationStyle: String) -> Unit
) {
    var primaryGoal by remember { mutableStateOf("Ace university exams & launch startup") }
    var workHours by remember { mutableStateOf("Afternoons (2:00 PM – 7:00 PM)") }
    var focusArea by remember { mutableStateOf("Connected task & calendar planning") }
    var notificationStyle by remember { mutableStateOf("Gentle & encouraging nudges") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Welcome to LifeOS",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    "LifeOS connects your tasks, exams, habits, and calendar into one intelligent personal operating system. Let's calibrate your preferences:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = primaryGoal,
                    onValueChange = { primaryGoal = it },
                    label = { Text("What are your main goals right now?") },
                    modifier = Modifier.fillMaxWidth().testTag("onboarding_goal_input")
                )

                OutlinedTextField(
                    value = workHours,
                    onValueChange = { workHours = it },
                    label = { Text("What are your typical deep work / study hours?") },
                    modifier = Modifier.fillMaxWidth().testTag("onboarding_hours_input")
                )

                OutlinedTextField(
                    value = focusArea,
                    onValueChange = { focusArea = it },
                    label = { Text("What do you want LifeOS to help with most?") },
                    modifier = Modifier.fillMaxWidth().testTag("onboarding_focus_input")
                )

                OutlinedTextField(
                    value = notificationStyle,
                    onValueChange = { notificationStyle = it },
                    label = { Text("Notification & reminder style") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onComplete(primaryGoal, workHours, focusArea, notificationStyle)
                    },
                    modifier = Modifier.fillMaxWidth().testTag("complete_onboarding_button")
                ) {
                    Text("Initialize My LifeOS System")
                }
            }
        }
    }
}
