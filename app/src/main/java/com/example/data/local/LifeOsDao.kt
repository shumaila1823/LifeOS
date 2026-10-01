package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LifeOsDao {
    // Tasks
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, priority DESC, deadlineEpochMs ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)

    // Subtasks
    @Query("SELECT * FROM subtasks WHERE taskId = :taskId")
    fun getSubtasksForTask(taskId: String): Flow<List<SubtaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtask(subtask: SubtaskEntity)

    @Delete
    suspend fun deleteSubtask(subtask: SubtaskEntity)

    // Projects
    @Query("SELECT * FROM projects ORDER BY createdAtEpochMs DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    // Goals
    @Query("SELECT * FROM goals ORDER BY createdAtEpochMs DESC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    // Goal Milestones
    @Query("SELECT * FROM goal_milestones WHERE goalId = :goalId ORDER BY orderIndex ASC")
    fun getMilestonesForGoal(goalId: String): Flow<List<GoalMilestoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoalMilestone(milestone: GoalMilestoneEntity)

    @Update
    suspend fun updateGoalMilestone(milestone: GoalMilestoneEntity)

    // Habits
    @Query("SELECT * FROM habits ORDER BY createdAtEpochMs DESC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    // Habit Logs
    @Query("SELECT * FROM habit_logs WHERE dateString = :dateString")
    fun getHabitLogsForDate(dateString: String): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId")
    fun getHabitLogsForHabit(habitId: String): Flow<List<HabitLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitLog(log: HabitLogEntity)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND dateString = :dateString")
    suspend fun deleteHabitLog(habitId: String, dateString: String)

    // Calendar Events
    @Query("SELECT * FROM calendar_events ORDER BY startEpochMs ASC")
    fun getAllEvents(): Flow<List<CalendarEventEntity>>

    @Query("SELECT * FROM calendar_events WHERE startEpochMs >= :startMs AND endEpochMs <= :endMs ORDER BY startEpochMs ASC")
    fun getEventsBetween(startMs: Long, endMs: Long): Flow<List<CalendarEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEventEntity)

    @Update
    suspend fun updateEvent(event: CalendarEventEntity)

    @Delete
    suspend fun deleteEvent(event: CalendarEventEntity)

    // Notes
    @Query("SELECT * FROM notes ORDER BY isPinned DESC, updatedAtEpochMs DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    // Study Subjects & Topics
    @Query("SELECT * FROM study_subjects")
    fun getAllStudySubjects(): Flow<List<StudySubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySubject(subject: StudySubjectEntity)

    @Delete
    suspend fun deleteStudySubject(subject: StudySubjectEntity)

    @Query("SELECT * FROM study_topics WHERE subjectId = :subjectId ORDER BY chapterNumber ASC")
    fun getTopicsForSubject(subjectId: String): Flow<List<StudyTopicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyTopic(topic: StudyTopicEntity)

    @Update
    suspend fun updateStudyTopic(topic: StudyTopicEntity)

    // Flashcards & Quizzes
    @Query("SELECT * FROM flashcards WHERE subjectId = :subjectId")
    fun getFlashcardsForSubject(subjectId: String): Flow<List<FlashcardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: FlashcardEntity)

    @Query("SELECT * FROM quizzes WHERE subjectId = :subjectId")
    fun getQuizzesForSubject(subjectId: String): Flow<List<QuizQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizQuestion(quiz: QuizQuestionEntity)

    // AI Memories
    @Query("SELECT * FROM ai_memories ORDER BY createdAtEpochMs DESC")
    fun getAllAiMemories(): Flow<List<AiMemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAiMemory(memory: AiMemoryEntity)

    @Delete
    suspend fun deleteAiMemory(memory: AiMemoryEntity)

    // Chat Messages
    @Query("SELECT * FROM chat_messages ORDER BY timestampEpochMs ASC")
    fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(msg: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatMessages()

    // Global clear for demo reset
    @Query("DELETE FROM tasks")
    suspend fun clearTasks()

    @Query("DELETE FROM projects")
    suspend fun clearProjects()

    @Query("DELETE FROM goals")
    suspend fun clearGoals()

    @Query("DELETE FROM habits")
    suspend fun clearHabits()

    @Query("DELETE FROM calendar_events")
    suspend fun clearEvents()

    @Query("DELETE FROM notes")
    suspend fun clearNotes()

    @Query("DELETE FROM study_subjects")
    suspend fun clearStudySubjects()

    @Query("DELETE FROM study_topics")
    suspend fun clearStudyTopics()
}
