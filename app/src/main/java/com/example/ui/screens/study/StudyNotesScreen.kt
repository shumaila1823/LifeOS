package com.example.ui.screens.study

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.NoteEntity
import com.example.data.model.StudySubjectEntity
import com.example.ui.components.CreateNoteDialog
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.PurpleStudy
import com.example.ui.theme.RoseCritical
import com.example.ui.viewmodel.PomodoroState
import java.util.Locale

@Composable
fun StudyNotesScreen(
    studySubjects: List<StudySubjectEntity>,
    notes: List<NoteEntity>,
    pomodoroState: PomodoroState,
    onStartPomodoro: () -> Unit,
    onPausePomodoro: () -> Unit,
    onResetPomodoro: () -> Unit,
    onCreateNote: (title: String, content: String, category: String) -> Unit,
    onDeleteNote: (NoteEntity) -> Unit,
    onSummarizeNote: (NoteEntity) -> Unit,
    onExtractTasksFromNote: (NoteEntity) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Study Mode, 1: Notes
    var showNoteDialog by remember { mutableStateOf(false) }
    var showFlashcardsDialog by remember { mutableStateOf(false) }
    var showQuizDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = { showNoteDialog = true },
                    modifier = Modifier.testTag("add_note_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add note")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("study_notes_screen")
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = if (selectedTab == 0) "Study Mode & Focus" else "Intelligent Notes",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Study Mode") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Notes (${notes.size})") }
                )
            }

            if (selectedTab == 0) {
                // Study Mode
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Pomodoro Timer Widget
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth().testTag("pomodoro_card")
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(18.dp).fillMaxWidth()
                            ) {
                                Text(
                                    text = if (pomodoroState.isBreak) "☕ REST / BREAK INTERVAL" else "🧠 DEEP FOCUS INTERVAL",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (pomodoroState.isBreak) EmeraldSuccess else MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                val mins = pomodoroState.secondsRemaining / 60
                                val secs = pomodoroState.secondsRemaining % 60
                                val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs)

                                Text(
                                    text = timeFormatted,
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "${pomodoroState.completedCount} pomodoro sessions completed today",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    if (pomodoroState.isRunning) {
                                        Button(
                                            onClick = onPausePomodoro,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                            modifier = Modifier.testTag("pause_pomodoro_button")
                                        ) {
                                            Icon(Icons.Default.Pause, contentDescription = null)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Pause")
                                        }
                                    } else {
                                        Button(
                                            onClick = onStartPomodoro,
                                            modifier = Modifier.testTag("start_pomodoro_button")
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Start Focus")
                                        }
                                    }
                                    OutlinedButton(onClick = onResetPomodoro) {
                                        Icon(Icons.Default.Refresh, contentDescription = null)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Reset")
                                    }
                                }
                            }
                        }
                    }

                    // Weak-Topic Detection & AI Recommendation
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.WarningAmber,
                                        contentDescription = null,
                                        tint = RoseCritical
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Weak-Topic Detection & Recommendation",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Focus on Physics Chapter 4 (Electromagnetism) first. It currently has your lowest confidence rating (32%) and represents 30% of your upcoming exam in 3 days.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    // Subject & Topics Confidence
                    item {
                        Text(
                            "Physics II: Topic Confidence Levels",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ConfidenceTopicRow("Chapter 1: Electric Charges & Fields", 90, EmeraldSuccess)
                                ConfidenceTopicRow("Chapter 2: Electrostatic Potential", 75, EmeraldSuccess)
                                ConfidenceTopicRow("Chapter 3: Current Electricity & Circuits", 48, Color(0xFFF59E0B))
                                ConfidenceTopicRow("Chapter 4: Electromagnetic Induction", 32, RoseCritical)
                            }
                        }
                    }

                    // Interactive Flashcards & Quiz Action Buttons
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { showFlashcardsDialog = true },
                                modifier = Modifier.weight(1f).testTag("open_flashcards_button")
                            ) {
                                Icon(Icons.Default.Style, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Flashcards")
                            }
                            Button(
                                onClick = { showQuizDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PurpleStudy),
                                modifier = Modifier.weight(1f).testTag("open_quiz_button")
                            ) {
                                Icon(Icons.Default.Quiz, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Practice Quiz")
                            }
                        }
                    }
                }
            } else {
                // Notes View
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(notes) { note ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
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
                                        text = note.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = note.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = note.content,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // AI Actions on Note
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = { onSummarizeNote(note) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("AI Summary", fontSize = 11.sp)
                                    }
                                    OutlinedButton(
                                        onClick = { onExtractTasksFromNote(note) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Checklist, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Extract Tasks", fontSize = 11.sp)
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    IconButton(onClick = { onDeleteNote(note) }) {
                                        Icon(
                                            Icons.Default.DeleteOutline,
                                            contentDescription = "Delete note",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNoteDialog) {
        CreateNoteDialog(
            onDismiss = { showNoteDialog = false },
            onSave = { title, content, cat ->
                onCreateNote(title, content, cat)
                showNoteDialog = false
            }
        )
    }

    if (showFlashcardsDialog) {
        FlashcardsDeckDialog(onDismiss = { showFlashcardsDialog = false })
    }

    if (showQuizDialog) {
        PracticeQuizDialog(onDismiss = { showQuizDialog = false })
    }
}

@Composable
fun ConfidenceTopicRow(title: String, percentage: Int, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text("$percentage%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percentage / 100f },
            color = color,
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
        )
    }
}

@Composable
fun FlashcardsDeckDialog(onDismiss: () -> Unit) {
    val cards = remember {
        listOf(
            Pair("What is Faraday's Law of Electromagnetic Induction?", "EMF = -dPhi/dt. The induced electromotive force in any closed loop is equal to the negative rate of change of magnetic flux through it."),
            Pair("What does Lenz's Law state?", "The direction of the induced electric current always opposes the magnetic flux change that produced it (a consequence of energy conservation)."),
            Pair("What is Gauss's Law for Magnetism?", "Net magnetic flux through any closed Gaussian surface equals zero (integral B . dA = 0), confirming that isolated magnetic monopoles do not exist.")
        )
    }
    var currentIndex by remember { mutableStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth().height(360.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Flashcard ${currentIndex + 1} of ${cards.size}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFlipped) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 12.dp)
                        .clickable { isFlipped = !isFlipped }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isFlipped) "ANSWER" else "QUESTION (Tap to flip)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isFlipped) cards[currentIndex].second else cards[currentIndex].first,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = {
                            if (currentIndex > 0) {
                                currentIndex--
                                isFlipped = false
                            }
                        },
                        enabled = currentIndex > 0
                    ) {
                        Text("Previous")
                    }
                    TextButton(
                        onClick = {
                            if (currentIndex < cards.size - 1) {
                                currentIndex++
                                isFlipped = false
                            }
                        },
                        enabled = currentIndex < cards.size - 1
                    ) {
                        Text("Next Card")
                    }
                }
            }
        }
    }
}

@Composable
fun PracticeQuizDialog(onDismiss: () -> Unit) {
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }

    val question = "Which fundamental law states that magnetic field lines never terminate and magnetic monopoles do not exist?"
    val options = listOf(
        "Gauss's Law for Magnetism",
        "Coulomb's Law",
        "Faraday's Law of Induction",
        "Ampere's Circuital Law"
    )
    val correctIndex = 0

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Physics Quiz Check", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(question, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)

                options.forEachIndexed { idx, opt ->
                    val isChosen = selectedOption == idx
                    val isCorrect = idx == correctIndex
                    val cardColor = when {
                        !hasAnswered && isChosen -> MaterialTheme.colorScheme.primaryContainer
                        hasAnswered && isCorrect -> EmeraldSuccess.copy(alpha = 0.2f)
                        hasAnswered && isChosen && !isCorrect -> RoseCritical.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }

                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !hasAnswered) {
                                selectedOption = idx
                                hasAnswered = true
                            }
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${('A' + idx)}. $opt",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                if (hasAnswered) {
                    val correct = selectedOption == correctIndex
                    Text(
                        text = if (correct) "✓ Correct! Gauss's Law states net magnetic flux is always zero." else "✗ Incorrect. Gauss's Law for Magnetism explains why monopoles don't exist.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (correct) EmeraldSuccess else RoseCritical
                    )
                }
            }
        }
    }
}
