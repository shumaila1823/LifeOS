package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AiMemoryEntity
import com.example.ui.components.*
import com.example.ui.navigation.LifeOsScreen
import com.example.ui.screens.ai.AiAssistantScreen
import com.example.ui.screens.calendar.CalendarScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.habits.HabitsGoalsScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.study.StudyNotesScreen
import com.example.ui.screens.tasks.TasksScreen
import com.example.ui.theme.LifeOsTheme
import com.example.ui.viewmodel.LifeOsViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LifeOsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LifeOsTheme {
                LifeOsApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LifeOsApp(viewModel: LifeOsViewModel) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(LifeOsScreen.Dashboard) }
    var showGlobalSearch by remember { mutableStateOf(false) }
    var showOnboarding by remember { mutableStateOf(false) }

    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val habitLogsToday by viewModel.habitLogsToday.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val studySubjects by viewModel.studySubjects.collectAsStateWithLifecycle()
    val aiMemories by viewModel.aiMemories.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val taskFilter by viewModel.taskFilter.collectAsStateWithLifecycle()
    val isAiThinking by viewModel.isAiThinking.collectAsStateWithLifecycle()
    val pendingConfirmation by viewModel.pendingConfirmation.collectAsStateWithLifecycle()
    val dailyBriefing by viewModel.dailyBriefing.collectAsStateWithLifecycle()
    val weeklyReview by viewModel.weeklyReview.collectAsStateWithLifecycle()
    val pomodoroState by viewModel.pomodoroState.collectAsStateWithLifecycle()

    // BackHandler: Return to Dashboard if on a sub-screen
    BackHandler(enabled = currentScreen != LifeOsScreen.Dashboard) {
        currentScreen = LifeOsScreen.Dashboard
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("lifeos_bottom_nav"),
                tonalElevation = 6.dp
            ) {
                LifeOsScreen.values().forEach { screen ->
                    val selected = currentScreen == screen
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                if (selected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.label
                            )
                        },
                        label = {
                            Text(
                                screen.label,
                                maxLines = 1,
                                fontSize = 10.sp
                            )
                        },
                        modifier = Modifier.testTag("nav_${screen.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                LifeOsScreen.Dashboard -> {
                    DashboardScreen(
                        tasks = tasks,
                        events = events,
                        habits = habits,
                        habitLogsToday = habitLogsToday,
                        goals = goals,
                        studySubjects = studySubjects,
                        onNavigate = { currentScreen = it },
                        onToggleTask = { viewModel.toggleTaskCompletion(it) },
                        onToggleHabit = { viewModel.toggleHabitToday(it) },
                        onSubmitPrompt = { viewModel.submitAiPrompt(it) },
                        onOpenDailyBriefing = { viewModel.loadDailyBriefing() },
                        onOpenWeeklyReview = { viewModel.loadWeeklyReview() },
                        onOpenSearch = { showGlobalSearch = true }
                    )
                }
                LifeOsScreen.Tasks -> {
                    TasksScreen(
                        tasks = tasks,
                        projects = projects,
                        filter = taskFilter,
                        onFilterChange = { viewModel.setTaskFilter(it) },
                        onToggleTask = { viewModel.toggleTaskCompletion(it) },
                        onDeleteTask = { viewModel.deleteTaskWithConfirmation(it) },
                        onCreateTask = {
                            viewModel.viewModelScopeLaunch {
                                viewModel.repository.insertTask(it)
                            }
                        },
                        onCreateProject = {
                            viewModel.viewModelScopeLaunch {
                                viewModel.repository.insertProject(it)
                            }
                        }
                    )
                }
                LifeOsScreen.Calendar -> {
                    CalendarScreen(
                        events = events,
                        tasks = tasks,
                        onAddEvent = { title, start, end, loc, notesStr ->
                            viewModel.createCalendarEvent(title, start, end, loc, notesStr)
                        },
                        onDeleteEvent = {
                            viewModel.viewModelScopeLaunch {
                                viewModel.repository.deleteEvent(it)
                            }
                        },
                        onScheduleTask = { task, startEpochMs ->
                            viewModel.createCalendarEvent(
                                title = task.title,
                                startMs = startEpochMs,
                                endMs = startEpochMs + (task.estimatedMinutes * 60 * 1000L),
                                location = "Dedicated Work Block",
                                notes = "Linked to task: ${task.title}"
                            )
                            Toast.makeText(context, "Scheduled '${task.title}' on your calendar", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                LifeOsScreen.HabitsGoals -> {
                    HabitsGoalsScreen(
                        habits = habits,
                        habitLogsToday = habitLogsToday,
                        goals = goals,
                        onToggleHabit = { viewModel.toggleHabitToday(it) },
                        onDeleteHabit = {
                            viewModel.viewModelScopeLaunch {
                                viewModel.repository.deleteHabit(it)
                            }
                        },
                        onCreateHabit = { name, freq, rem ->
                            viewModel.createHabit(name, freq, rem)
                        },
                        onCreateGoal = { title, desc, deadline ->
                            viewModel.createGoal(title, desc, deadline)
                        }
                    )
                }
                LifeOsScreen.StudyNotes -> {
                    StudyNotesScreen(
                        studySubjects = studySubjects,
                        notes = notes,
                        pomodoroState = pomodoroState,
                        onStartPomodoro = { viewModel.startPomodoro() },
                        onPausePomodoro = { viewModel.pausePomodoro() },
                        onResetPomodoro = { viewModel.resetPomodoro() },
                        onCreateNote = { title, content, cat ->
                            viewModel.createNote(title, content, cat)
                        },
                        onDeleteNote = {
                            viewModel.viewModelScopeLaunch {
                                viewModel.repository.deleteNote(it)
                            }
                        },
                        onSummarizeNote = { note ->
                            viewModel.viewModelScopeLaunch {
                                val summary = viewModel.aiEngine.summarizeNote(note)
                                Toast.makeText(context, summary, Toast.LENGTH_LONG).show()
                            }
                        },
                        onExtractTasksFromNote = { note ->
                            viewModel.viewModelScopeLaunch {
                                val extracted = viewModel.aiEngine.extractTasksFromNote(note)
                                Toast.makeText(context, "Extracted ${extracted.size} tasks into your queue!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
                LifeOsScreen.AiAssistant -> {
                    AiAssistantScreen(
                        messages = chatMessages,
                        isThinking = isAiThinking,
                        onSendMessage = { viewModel.submitAiPrompt(it) },
                        onClearHistory = {
                            viewModel.viewModelScopeLaunch {
                                viewModel.repository.clearChatMessages()
                            }
                        }
                    )
                }
                LifeOsScreen.Settings -> {
                    SettingsScreen(
                        memories = aiMemories,
                        onAddMemory = { cat, txt ->
                            viewModel.viewModelScopeLaunch {
                                viewModel.repository.insertAiMemory(AiMemoryEntity(category = cat, memoryText = txt))
                            }
                        },
                        onDeleteMemory = { mem ->
                            viewModel.viewModelScopeLaunch {
                                viewModel.repository.deleteAiMemory(mem)
                            }
                        },
                        onResetDemoData = {
                            viewModel.resetToDemoData()
                            Toast.makeText(context, "LifeOS reset to demo data", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // Confirmation Dialog for Destructive Operations
    if (pendingConfirmation != null) {
        ConfirmationDialog(
            title = pendingConfirmation!!.title,
            message = pendingConfirmation!!.message,
            onConfirm = { viewModel.confirmPendingAction() },
            onDismiss = { viewModel.clearConfirmation() }
        )
    }

    // Daily Briefing Dialog
    if (dailyBriefing != null) {
        DailyBriefingDialog(
            briefingText = dailyBriefing!!,
            onDismiss = { viewModel.dismissDailyBriefing() }
        )
    }

    // Weekly Review Dialog
    if (weeklyReview != null) {
        WeeklyReviewDialog(
            reviewText = weeklyReview!!,
            onDismiss = { viewModel.dismissWeeklyReview() }
        )
    }

    // Global Search Dialog
    if (showGlobalSearch) {
        GlobalSearchDialog(
            tasks = tasks,
            notes = notes,
            projects = projects,
            goals = goals,
            events = events,
            habits = habits,
            studySubjects = studySubjects,
            onDismiss = { showGlobalSearch = false },
            onSelectItem = { itemInfo ->
                showGlobalSearch = false
                Toast.makeText(context, itemInfo, Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Onboarding Dialog
    if (showOnboarding) {
        OnboardingDialog(
            onDismiss = { showOnboarding = false },
            onComplete = { goalsStr, hoursStr, focusStr, notifStr ->
                viewModel.viewModelScopeLaunch {
                    viewModel.repository.insertAiMemory(AiMemoryEntity(category = "Primary Goal", memoryText = goalsStr))
                    viewModel.repository.insertAiMemory(AiMemoryEntity(category = "Work Hours", memoryText = hoursStr))
                    viewModel.repository.insertAiMemory(AiMemoryEntity(category = "Focus Area", memoryText = focusStr))
                    viewModel.repository.insertAiMemory(AiMemoryEntity(category = "Notification Style", memoryText = notifStr))
                }
                showOnboarding = false
                Toast.makeText(context, "Welcome to LifeOS! Personal operating system configured.", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
