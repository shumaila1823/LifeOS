package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class Priority {
    LOW, MEDIUM, HIGH, CRITICAL
}

enum class Recurrence {
    NONE, DAILY, WEEKDAYS, WEEKLY, MONTHLY
}

enum class ProjectHealth {
    ON_TRACK, AT_RISK, BEHIND
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val deadlineEpochMs: Long? = null,
    val estimatedMinutes: Int = 30,
    val isCompleted: Boolean = false,
    val completedEpochMs: Long? = null,
    val projectId: String? = null,
    val goalId: String? = null,
    val tags: String = "", // Comma-separated
    val recurrence: Recurrence = Recurrence.NONE,
    val scheduledEpochMs: Long? = null,
    val priorityReason: String = "",
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "subtasks")
data class SubtaskEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val taskId: String,
    val title: String,
    val isCompleted: Boolean = false
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val deadlineEpochMs: Long? = null,
    val health: ProjectHealth = ProjectHealth.ON_TRACK,
    val healthReason: String = "All milestones progressing smoothly",
    val colorHex: String = "#6366F1",
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val deadlineEpochMs: Long? = null,
    val targetValue: Float = 100f,
    val currentValue: Float = 0f,
    val unit: String = "%",
    val colorHex: String = "#10B981",
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "goal_milestones")
data class GoalMilestoneEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val goalId: String,
    val title: String,
    val isCompleted: Boolean = false,
    val orderIndex: Int = 0
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val frequency: String = "Daily", // Daily, 3x/week, etc.
    val targetCount: Int = 1,
    val reminderTime: String = "08:00",
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val colorHex: String = "#EC4899",
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "habit_logs")
data class HabitLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val habitId: String,
    val dateString: String, // Format: YYYY-MM-DD
    val completedCount: Int = 1,
    val timestampEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val startEpochMs: Long,
    val endEpochMs: Long,
    val location: String = "",
    val notes: String = "",
    val recurrence: Recurrence = Recurrence.NONE,
    val relatedTaskId: String? = null,
    val relatedProjectId: String? = null,
    val colorHex: String = "#3B82F6"
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val category: String = "General", // General, Study, Project, Meeting
    val isPinned: Boolean = false,
    val tags: String = "",
    val updatedAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_subjects")
data class StudySubjectEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val examDateEpochMs: Long? = null,
    val targetConfidence: Int = 85,
    val colorHex: String = "#8B5CF6"
)

@Entity(tableName = "study_topics")
data class StudyTopicEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val subjectId: String,
    val title: String,
    val chapterNumber: Int = 1,
    val confidenceScore: Int = 50, // 0 to 100
    val estimatedStudyMinutes: Int = 60,
    val isCompleted: Boolean = false
)

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val subjectId: String,
    val question: String,
    val answer: String,
    val lastReviewedEpochMs: Long? = null,
    val masteryLevel: Int = 0 // 0 to 5
)

@Entity(tableName = "quizzes")
data class QuizQuestionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val subjectId: String,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOptionIndex: Int, // 0, 1, 2, 3
    val explanation: String = ""
)

@Entity(tableName = "ai_memories")
data class AiMemoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val category: String, // Work Hours, Study Habit, Goal, Preference
    val memoryText: String,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val structuredActionJson: String? = null,
    val timestampEpochMs: Long = System.currentTimeMillis()
)
