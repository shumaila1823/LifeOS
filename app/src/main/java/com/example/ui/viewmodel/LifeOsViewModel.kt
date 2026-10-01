package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiActionResult
import com.example.data.ai.LifeOsAiEngine
import com.example.data.local.LifeOsDatabase
import com.example.data.model.*
import com.example.data.repository.LifeOsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class TaskFilterType {
    ALL, TODAY, UPCOMING, OVERDUE, COMPLETED, HIGH_PRIORITY
}

data class ConfirmationDialogData(
    val title: String,
    val message: String,
    val onConfirm: suspend () -> Unit
)

data class PomodoroState(
    val secondsRemaining: Int = 25 * 60,
    val isRunning: Boolean = false,
    val isBreak: Boolean = false,
    val completedCount: Int = 0
)

class LifeOsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = LifeOsDatabase.getDatabase(application)
    val repository = LifeOsRepository(db.dao())
    val aiEngine = LifeOsAiEngine(repository)

    val tasks = repository.tasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val projects = repository.projects.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val goals = repository.goals.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val habits = repository.habits.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val events = repository.events.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val notes = repository.notes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val studySubjects = repository.studySubjects.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val aiMemories = repository.aiMemories.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val chatMessages = repository.chatMessages.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _taskFilter = MutableStateFlow(TaskFilterType.ALL)
    val taskFilter = _taskFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking = _isAiThinking.asStateFlow()

    private val _pendingConfirmation = MutableStateFlow<ConfirmationDialogData?>(null)
    val pendingConfirmation = _pendingConfirmation.asStateFlow()

    private val _dailyBriefing = MutableStateFlow<String?>(null)
    val dailyBriefing = _dailyBriefing.asStateFlow()

    private val _weeklyReview = MutableStateFlow<String?>(null)
    val weeklyReview = _weeklyReview.asStateFlow()

    private val _pomodoroState = MutableStateFlow(PomodoroState())
    val pomodoroState = _pomodoroState.asStateFlow()

    private var pomodoroJob: Job? = null

    val todayDateString: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val habitLogsToday = repository.getHabitLogs(todayDateString)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    fun setTaskFilter(filter: TaskFilterType) {
        _taskFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            val updated = task.copy(
                isCompleted = !task.isCompleted,
                completedEpochMs = if (!task.isCompleted) System.currentTimeMillis() else null
            )
            repository.updateTask(updated)
        }
    }

    fun deleteTaskWithConfirmation(task: TaskEntity) {
        _pendingConfirmation.value = ConfirmationDialogData(
            title = "Delete Task",
            message = "Are you sure you want to delete '${task.title}'? This action cannot be undone.",
            onConfirm = {
                repository.deleteTask(task)
            }
        )
    }

    fun clearConfirmation() {
        _pendingConfirmation.value = null
    }

    fun confirmPendingAction() {
        val pending = _pendingConfirmation.value ?: return
        viewModelScope.launch {
            pending.onConfirm()
            _pendingConfirmation.value = null
        }
    }

    fun toggleHabitToday(habitId: String) {
        viewModelScope.launch {
            repository.toggleHabitForDate(habitId, todayDateString)
            // also update habit streak count
            val habitList = habits.value
            val habit = habitList.find { it.id == habitId }
            if (habit != null) {
                val isCompleted = habitLogsToday.value.any { it.habitId == habitId }
                val newStreak = if (!isCompleted) habit.currentStreak + 1 else maxOf(0, habit.currentStreak - 1)
                val newBest = maxOf(habit.bestStreak, newStreak)
                repository.updateHabit(habit.copy(currentStreak = newStreak, bestStreak = newBest))
            }
        }
    }

    // AI Prompt submission
    fun submitAiPrompt(userInput: String) {
        if (userInput.isBlank()) return
        viewModelScope.launch {
            // Save user message
            repository.insertChatMessage(ChatMessageEntity(isUser = true, text = userInput))
            _isAiThinking.value = true

            val result = aiEngine.processUserPrompt(userInput)
            _isAiThinking.value = false

            when (result) {
                is AiActionResult.Success -> {
                    val fullReply = "${result.message}\n\nActions Executed:\n" +
                            result.actionsExecuted.joinToString("\n") { "✓ $it" }
                    repository.insertChatMessage(ChatMessageEntity(isUser = false, text = fullReply))
                }
                is AiActionResult.Informational -> {
                    repository.insertChatMessage(ChatMessageEntity(isUser = false, text = result.answer))
                }
                is AiActionResult.RequiresConfirmation -> {
                    _pendingConfirmation.value = ConfirmationDialogData(
                        title = "Confirmation Required",
                        message = result.confirmationPrompt,
                        onConfirm = {
                            result.onConfirm()
                            repository.insertChatMessage(
                                ChatMessageEntity(isUser = false, text = "Action confirmed and executed successfully.")
                            )
                        }
                    )
                }
                is AiActionResult.Error -> {
                    repository.insertChatMessage(ChatMessageEntity(isUser = false, text = "⚠️ ${result.error}"))
                }
            }
        }
    }

    // Daily Briefing
    fun loadDailyBriefing() {
        viewModelScope.launch {
            _dailyBriefing.value = aiEngine.generateDailyBriefing()
        }
    }

    fun dismissDailyBriefing() {
        _dailyBriefing.value = null
    }

    // Weekly Review
    fun loadWeeklyReview() {
        viewModelScope.launch {
            _weeklyReview.value = aiEngine.generateWeeklyReview()
        }
    }

    fun dismissWeeklyReview() {
        _weeklyReview.value = null
    }

    // Pomodoro Timer
    fun startPomodoro() {
        if (_pomodoroState.value.isRunning) return
        _pomodoroState.value = _pomodoroState.value.copy(isRunning = true)
        pomodoroJob?.cancel()
        pomodoroJob = viewModelScope.launch {
            while (_pomodoroState.value.isRunning && _pomodoroState.value.secondsRemaining > 0) {
                delay(1000)
                _pomodoroState.value = _pomodoroState.value.copy(
                    secondsRemaining = _pomodoroState.value.secondsRemaining - 1
                )
            }
            if (_pomodoroState.value.secondsRemaining <= 0) {
                val nextIsBreak = !_pomodoroState.value.isBreak
                val newCount = if (!nextIsBreak) _pomodoroState.value.completedCount + 1 else _pomodoroState.value.completedCount
                _pomodoroState.value = PomodoroState(
                    secondsRemaining = if (nextIsBreak) 5 * 60 else 25 * 60,
                    isRunning = false,
                    isBreak = nextIsBreak,
                    completedCount = newCount
                )
            }
        }
    }

    fun pausePomodoro() {
        _pomodoroState.value = _pomodoroState.value.copy(isRunning = false)
        pomodoroJob?.cancel()
    }

    fun resetPomodoro() {
        pomodoroJob?.cancel()
        _pomodoroState.value = PomodoroState()
    }

    // Reset Demo Data
    fun resetToDemoData() {
        viewModelScope.launch {
            repository.resetAllData()
        }
    }

    // Save Quick Note
    fun createNote(title: String, content: String, category: String) {
        viewModelScope.launch {
            repository.insertNote(NoteEntity(title = title, content = content, category = category))
        }
    }

    fun createCalendarEvent(title: String, startMs: Long, endMs: Long, location: String, notes: String) {
        viewModelScope.launch {
            repository.insertEvent(
                CalendarEventEntity(
                    title = title,
                    startEpochMs = startMs,
                    endEpochMs = endMs,
                    location = location,
                    notes = notes
                )
            )
        }
    }

    fun createHabit(name: String, frequency: String, reminderTime: String) {
        viewModelScope.launch {
            repository.insertHabit(
                HabitEntity(
                    name = name,
                    frequency = frequency,
                    reminderTime = reminderTime,
                    colorHex = "#10B981"
                )
            )
        }
    }

    fun createGoal(title: String, description: String, deadlineMs: Long?) {
        viewModelScope.launch {
            repository.insertGoal(
                GoalEntity(
                    title = title,
                    description = description,
                    deadlineEpochMs = deadlineMs
                )
            )
        }
    }

    fun viewModelScopeLaunch(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }
}
