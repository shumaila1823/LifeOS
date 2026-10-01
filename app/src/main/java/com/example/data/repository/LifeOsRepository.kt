package com.example.data.repository

import com.example.data.local.LifeOsDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*

class LifeOsRepository(private val dao: LifeOsDao) {

    val tasks: Flow<List<TaskEntity>> = dao.getAllTasks()
    val projects: Flow<List<ProjectEntity>> = dao.getAllProjects()
    val goals: Flow<List<GoalEntity>> = dao.getAllGoals()
    val habits: Flow<List<HabitEntity>> = dao.getAllHabits()
    val events: Flow<List<CalendarEventEntity>> = dao.getAllEvents()
    val notes: Flow<List<NoteEntity>> = dao.getAllNotes()
    val studySubjects: Flow<List<StudySubjectEntity>> = dao.getAllStudySubjects()
    val aiMemories: Flow<List<AiMemoryEntity>> = dao.getAllAiMemories()
    val chatMessages: Flow<List<ChatMessageEntity>> = dao.getAllChatMessages()

    fun getSubtasks(taskId: String): Flow<List<SubtaskEntity>> = dao.getSubtasksForTask(taskId)
    fun getMilestones(goalId: String): Flow<List<GoalMilestoneEntity>> = dao.getMilestonesForGoal(goalId)
    fun getHabitLogs(dateString: String): Flow<List<HabitLogEntity>> = dao.getHabitLogsForDate(dateString)
    fun getStudyTopics(subjectId: String): Flow<List<StudyTopicEntity>> = dao.getTopicsForSubject(subjectId)
    fun getFlashcards(subjectId: String): Flow<List<FlashcardEntity>> = dao.getFlashcardsForSubject(subjectId)
    fun getQuizzes(subjectId: String): Flow<List<QuizQuestionEntity>> = dao.getQuizzesForSubject(subjectId)

    suspend fun insertTask(task: TaskEntity) = dao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = dao.updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = dao.deleteTask(task)
    suspend fun deleteTaskById(id: String) = dao.deleteTaskById(id)

    suspend fun insertSubtask(subtask: SubtaskEntity) = dao.insertSubtask(subtask)
    suspend fun deleteSubtask(subtask: SubtaskEntity) = dao.deleteSubtask(subtask)

    suspend fun insertProject(project: ProjectEntity) = dao.insertProject(project)
    suspend fun updateProject(project: ProjectEntity) = dao.updateProject(project)
    suspend fun deleteProject(project: ProjectEntity) = dao.deleteProject(project)

    suspend fun insertGoal(goal: GoalEntity) = dao.insertGoal(goal)
    suspend fun updateGoal(goal: GoalEntity) = dao.updateGoal(goal)
    suspend fun deleteGoal(goal: GoalEntity) = dao.deleteGoal(goal)

    suspend fun insertGoalMilestone(milestone: GoalMilestoneEntity) = dao.insertGoalMilestone(milestone)
    suspend fun updateGoalMilestone(milestone: GoalMilestoneEntity) = dao.updateGoalMilestone(milestone)

    suspend fun insertHabit(habit: HabitEntity) = dao.insertHabit(habit)
    suspend fun updateHabit(habit: HabitEntity) = dao.updateHabit(habit)
    suspend fun deleteHabit(habit: HabitEntity) = dao.deleteHabit(habit)

    suspend fun toggleHabitForDate(habitId: String, dateString: String) {
        val existing = dao.getHabitLogsForDate(dateString).firstOrNull()?.find { it.habitId == habitId }
        if (existing != null) {
            dao.deleteHabitLog(habitId, dateString)
        } else {
            dao.insertHabitLog(HabitLogEntity(habitId = habitId, dateString = dateString))
        }
    }

    suspend fun insertEvent(event: CalendarEventEntity) = dao.insertEvent(event)
    suspend fun updateEvent(event: CalendarEventEntity) = dao.updateEvent(event)
    suspend fun deleteEvent(event: CalendarEventEntity) = dao.deleteEvent(event)

    suspend fun insertNote(note: NoteEntity) = dao.insertNote(note)
    suspend fun updateNote(note: NoteEntity) = dao.updateNote(note)
    suspend fun deleteNote(note: NoteEntity) = dao.deleteNote(note)

    suspend fun insertStudySubject(subject: StudySubjectEntity) = dao.insertStudySubject(subject)
    suspend fun deleteStudySubject(subject: StudySubjectEntity) = dao.deleteStudySubject(subject)
    suspend fun insertStudyTopic(topic: StudyTopicEntity) = dao.insertStudyTopic(topic)
    suspend fun updateStudyTopic(topic: StudyTopicEntity) = dao.updateStudyTopic(topic)
    suspend fun insertFlashcard(flashcard: FlashcardEntity) = dao.insertFlashcard(flashcard)
    suspend fun insertQuizQuestion(quiz: QuizQuestionEntity) = dao.insertQuizQuestion(quiz)

    suspend fun insertAiMemory(memory: AiMemoryEntity) = dao.insertAiMemory(memory)
    suspend fun deleteAiMemory(memory: AiMemoryEntity) = dao.deleteAiMemory(memory)

    suspend fun insertChatMessage(msg: ChatMessageEntity) = dao.insertChatMessage(msg)
    suspend fun clearChatMessages() = dao.clearChatMessages()

    suspend fun checkAndSeedInitialData() {
        val currentTasks = dao.getAllTasks().firstOrNull()
        if (currentTasks.isNullOrEmpty()) {
            seedDemoData()
        }
    }

    suspend fun seedDemoData() {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Projects
        val project1 = ProjectEntity(
            id = "proj_website",
            name = "Launch My Website",
            description = "Build personal portfolio & SaaS launch landing page",
            deadlineEpochMs = now + 14L * 24 * 3600 * 1000,
            health = ProjectHealth.ON_TRACK,
            healthReason = "Frontend and content milestones on schedule",
            colorHex = "#6366F1"
        )
        val project2 = ProjectEntity(
            id = "proj_physics",
            name = "Physics Final Prep",
            description = "Master chapters 1 through 4 for upcoming exam",
            deadlineEpochMs = now + 3L * 24 * 3600 * 1000,
            health = ProjectHealth.AT_RISK,
            healthReason = "Chapter 4 (Electromagnetism) confidence is low (32%) with exam in 3 days",
            colorHex = "#EF4444"
        )
        dao.insertProject(project1)
        dao.insertProject(project2)

        // Goals
        val goal1 = GoalEntity(
            id = "goal_python",
            title = "Master Python & AI Systems",
            description = "Complete curriculum and build automated agents",
            deadlineEpochMs = now + 45L * 24 * 3600 * 1000,
            targetValue = 100f,
            currentValue = 62f,
            unit = "%",
            colorHex = "#10B981"
        )
        val goal2 = GoalEntity(
            id = "goal_physics",
            title = "Score A in Physics Final",
            description = "Target >90% on comprehensive exam",
            deadlineEpochMs = now + 3L * 24 * 3600 * 1000,
            targetValue = 100f,
            currentValue = 65f,
            unit = "%",
            colorHex = "#8B5CF6"
        )
        dao.insertGoal(goal1)
        dao.insertGoal(goal2)

        // Goal Milestones
        dao.insertGoalMilestone(GoalMilestoneEntity(goalId = "goal_python", title = "Python Fundamentals", isCompleted = true, orderIndex = 0))
        dao.insertGoalMilestone(GoalMilestoneEntity(goalId = "goal_python", title = "Object-Oriented Programming", isCompleted = true, orderIndex = 1))
        dao.insertGoalMilestone(GoalMilestoneEntity(goalId = "goal_python", title = "REST APIs & Data Models", isCompleted = false, orderIndex = 2))
        dao.insertGoalMilestone(GoalMilestoneEntity(goalId = "goal_python", title = "Build Final Agent Project", isCompleted = false, orderIndex = 3))

        dao.insertGoalMilestone(GoalMilestoneEntity(goalId = "goal_physics", title = "Mechanics Review", isCompleted = true, orderIndex = 0))
        dao.insertGoalMilestone(GoalMilestoneEntity(goalId = "goal_physics", title = "Thermodynamics Quiz", isCompleted = true, orderIndex = 1))
        dao.insertGoalMilestone(GoalMilestoneEntity(goalId = "goal_physics", title = "Electromagnetism Ch 4", isCompleted = false, orderIndex = 2))

        // Tasks
        val task1 = TaskEntity(
            id = "task_physics_ch4",
            title = "Finish Physics Chapter 4 (Electromagnetism)",
            description = "Read key derivations, solve practice problems 4.1 to 4.12",
            priority = Priority.CRITICAL,
            deadlineEpochMs = now + 3L * 24 * 3600 * 1000,
            estimatedMinutes = 75,
            isCompleted = false,
            projectId = "proj_physics",
            goalId = "goal_physics",
            tags = "Study, Exam, Urgent",
            priorityReason = "Your Physics exam is in 3 days and Chapter 4 is currently incomplete (confidence: 32%)."
        )
        val task2 = TaskEntity(
            id = "task_website",
            title = "Finalize website responsive layout",
            description = "Check tablet breakpoints and dark mode styling",
            priority = Priority.HIGH,
            deadlineEpochMs = now + 2L * 24 * 3600 * 1000,
            estimatedMinutes = 90,
            isCompleted = false,
            projectId = "proj_website",
            tags = "Dev, Design",
            priorityReason = "Launch deadline is in 2 weeks; responsive pass unblocks beta testers."
        )
        val task3 = TaskEntity(
            id = "task_workout",
            title = "Full Body Resistance Workout",
            description = "Chest, back, and core session",
            priority = Priority.MEDIUM,
            deadlineEpochMs = now + 24 * 3600 * 1000,
            estimatedMinutes = 50,
            isCompleted = false,
            tags = "Health, Habit",
            priorityReason = "Maintains weekly fitness consistency (2 of 3 sessions done)."
        )
        val task4 = TaskEntity(
            id = "task_call_ahmed",
            title = "Call Ahmed regarding project collaboration",
            description = "Discuss API schema and integration contract",
            priority = Priority.MEDIUM,
            deadlineEpochMs = now + 20 * 3600 * 1000,
            estimatedMinutes = 20,
            isCompleted = false,
            tags = "Work, Calls",
            priorityReason = "Scheduled call commitment."
        )
        val task5 = TaskEntity(
            id = "task_grocery",
            title = "Order healthy meal prep groceries",
            description = "Oats, eggs, salmon, greens, and fruit",
            priority = Priority.LOW,
            deadlineEpochMs = now + 48 * 3600 * 1000,
            estimatedMinutes = 15,
            isCompleted = true,
            completedEpochMs = now - 4 * 3600 * 1000,
            tags = "Personal"
        )
        dao.insertTask(task1)
        dao.insertTask(task2)
        dao.insertTask(task3)
        dao.insertTask(task4)
        dao.insertTask(task5)

        // Subtasks for task1
        dao.insertSubtask(SubtaskEntity(taskId = "task_physics_ch4", title = "Faraday's & Lenz's Law concepts", isCompleted = true))
        dao.insertSubtask(SubtaskEntity(taskId = "task_physics_ch4", title = "Solve textbook problems 4.1 - 4.6", isCompleted = false))
        dao.insertSubtask(SubtaskEntity(taskId = "task_physics_ch4", title = "Review flashcards & cheat sheet", isCompleted = false))

        // Habits
        val habit1 = HabitEntity(
            id = "habit_water",
            name = "Drink 2.5L Water",
            frequency = "Daily",
            targetCount = 1,
            reminderTime = "09:00",
            currentStreak = 6,
            bestStreak = 14,
            colorHex = "#0EA5E9"
        )
        val habit2 = HabitEntity(
            id = "habit_read",
            name = "Read 20 Minutes",
            frequency = "Daily",
            targetCount = 1,
            reminderTime = "21:30",
            currentStreak = 4,
            bestStreak = 12,
            colorHex = "#F59E0B"
        )
        val habit3 = HabitEntity(
            id = "habit_exercise",
            name = "Workout 45 Minutes",
            frequency = "3x / week",
            targetCount = 1,
            reminderTime = "17:30",
            currentStreak = 3,
            bestStreak = 8,
            colorHex = "#EC4899"
        )
        val habit4 = HabitEntity(
            id = "habit_meditate",
            name = "Mindfulness / Meditation",
            frequency = "Daily",
            targetCount = 1,
            reminderTime = "07:30",
            currentStreak = 5,
            bestStreak = 15,
            colorHex = "#10B981"
        )
        dao.insertHabit(habit1)
        dao.insertHabit(habit2)
        dao.insertHabit(habit3)
        dao.insertHabit(habit4)

        // Habit logs for today
        dao.insertHabitLog(HabitLogEntity(habitId = "habit_water", dateString = todayStr))
        dao.insertHabitLog(HabitLogEntity(habitId = "habit_meditate", dateString = todayStr))

        // Calendar Events
        calendar.time = Date()
        calendar.set(Calendar.HOUR_OF_DAY, 16)
        calendar.set(Calendar.MINUTE, 0)
        val eventStart1 = calendar.timeInMillis
        val eventEnd1 = eventStart1 + 75 * 60 * 1000

        calendar.set(Calendar.HOUR_OF_DAY, 18)
        calendar.set(Calendar.MINUTE, 0)
        val eventStart2 = calendar.timeInMillis
        val eventEnd2 = eventStart2 + 60 * 60 * 1000

        dao.insertEvent(
            CalendarEventEntity(
                id = "evt_study_phys",
                title = "Physics Deep Work: Chapter 4",
                startEpochMs = eventStart1,
                endEpochMs = eventEnd1,
                location = "Campus Library Study Room B",
                notes = "Focus on Electromagnetism derivations and problem set 4",
                relatedTaskId = "task_physics_ch4",
                colorHex = "#6366F1"
            )
        )
        dao.insertEvent(
            CalendarEventEntity(
                id = "evt_workout",
                title = "Evening Gym Session",
                startEpochMs = eventStart2,
                endEpochMs = eventEnd2,
                location = "Fitness Club",
                notes = "Upper body strength routine",
                relatedTaskId = "task_workout",
                colorHex = "#EC4899"
            )
        )

        // Study Subject & Topics
        val subject1 = StudySubjectEntity(
            id = "subj_physics",
            name = "Physics II: Electromagnetism & Modern Physics",
            examDateEpochMs = now + 3L * 24 * 3600 * 1000,
            targetConfidence = 85,
            colorHex = "#8B5CF6"
        )
        dao.insertStudySubject(subject1)

        dao.insertStudyTopic(StudyTopicEntity(subjectId = "subj_physics", title = "Chapter 1: Electric Charges & Fields", chapterNumber = 1, confidenceScore = 90, estimatedStudyMinutes = 45, isCompleted = true))
        dao.insertStudyTopic(StudyTopicEntity(subjectId = "subj_physics", title = "Chapter 2: Electrostatic Potential & Capacitance", chapterNumber = 2, confidenceScore = 75, estimatedStudyMinutes = 60, isCompleted = true))
        dao.insertStudyTopic(StudyTopicEntity(subjectId = "subj_physics", title = "Chapter 3: Current Electricity & Circuits", chapterNumber = 3, confidenceScore = 48, estimatedStudyMinutes = 60, isCompleted = false))
        dao.insertStudyTopic(StudyTopicEntity(subjectId = "subj_physics", title = "Chapter 4: Electromagnetic Induction", chapterNumber = 4, confidenceScore = 32, estimatedStudyMinutes = 75, isCompleted = false))

        // Flashcards
        dao.insertFlashcard(FlashcardEntity(subjectId = "subj_physics", question = "What is Faraday's Law of Electromagnetic Induction?", answer = "The magnitude of the induced electromotive force (EMF) is proportional to the rate of change of magnetic flux through the circuit: EMF = -dPhi/dt.", masteryLevel = 3))
        dao.insertFlashcard(FlashcardEntity(subjectId = "subj_physics", question = "What does Lenz's Law establish?", answer = "The induced current always flows in such a direction that its magnetic effect opposes the change in magnetic flux producing it.", masteryLevel = 2))
        dao.insertFlashcard(FlashcardEntity(subjectId = "subj_physics", question = "What is Gauss's Law for Magnetism in integral form?", answer = "The net magnetic flux through any closed Gaussian surface equals zero, confirming that magnetic monopoles do not exist.", masteryLevel = 4))

        // Quizzes
        dao.insertQuizQuestion(
            QuizQuestionEntity(
                subjectId = "subj_physics",
                question = "Which fundamental law implies that magnetic field lines never terminate and magnetic monopoles do not exist?",
                optionA = "Gauss's Law for Magnetism",
                optionB = "Coulomb's Law",
                optionC = "Faraday's Law",
                optionD = "Ampere's Circuital Law",
                correctOptionIndex = 0,
                explanation = "Gauss's Law for Magnetism states net magnetic flux through a closed surface is always zero (div B = 0)."
            )
        )
        dao.insertQuizQuestion(
            QuizQuestionEntity(
                subjectId = "subj_physics",
                question = "In the formula EMF = -N(dPhi/dt), the negative sign is a mathematical expression of which law?",
                optionA = "Ohm's Law",
                optionB = "Lenz's Law",
                optionC = "Biot-Savart Law",
                optionD = "Kirchhoff's Voltage Law",
                correctOptionIndex = 1,
                explanation = "Lenz's Law accounts for conservation of energy; induced EMF opposes the flux change."
            )
        )

        // Notes
        val note1 = NoteEntity(
            id = "note_kickoff",
            title = "Website Launch Architecture & Milestones",
            content = """Decisions:
1. Use Kotlin + Jetpack Compose with Material 3 for the mobile client.
2. Direct API integration with local Room persistence for zero-latency offline experience.
3. Design for calm, minimal SaaS aesthetics.

Action items:
- Complete mobile UI responsive layout by Friday.
- Integrate tool calling for AI assistant.
- Verify dark and light themes.""".trimIndent(),
            category = "Project",
            isPinned = true,
            tags = "Architecture, Sprint"
        )
        val note2 = NoteEntity(
            id = "note_physics_formula",
            title = "Physics Final: Core Equations",
            content = """- Electric Flux: Phi_E = integral(E . dA) = q_enc / epsilon_0
- Capacitance: C = Q / V
- Ohm's Law: V = I * R
- Magnetic Force: F = q(v x B)
- Faraday's Law: EMF = -dPhi_B / dt
- Inductance: L = N * Phi / I""".trimIndent(),
            category = "Study",
            isPinned = false,
            tags = "Physics, CheatSheet"
        )
        dao.insertNote(note1)
        dao.insertNote(note2)

        // AI Memories
        dao.insertAiMemory(AiMemoryEntity(category = "Work Schedule", memoryText = "Peak focus hours are typically between 2:00 PM and 6:00 PM in the afternoon."))
        dao.insertAiMemory(AiMemoryEntity(category = "Academic Goal", memoryText = "Major priority is scoring an A in Physics II final exam this week."))
        dao.insertAiMemory(AiMemoryEntity(category = "Tone Preference", memoryText = "Prefers supportive, realistic, non-judgmental habit nudges."))

        // Initial Chat Message
        dao.insertChatMessage(
            ChatMessageEntity(
                isUser = false,
                text = "Welcome to LifeOS! I'm your AI operating system. I've organized your upcoming Physics exam, website launch project, workouts, and daily schedule. Try telling me: 'I have a physics exam next Tuesday, I haven't studied chapter 4, I need to finish my website this weekend, and I want to work out three times this week' or 'Plan my week'."
            )
        )
    }

    suspend fun resetAllData() {
        dao.clearTasks()
        dao.clearProjects()
        dao.clearGoals()
        dao.clearHabits()
        dao.clearEvents()
        dao.clearNotes()
        dao.clearStudySubjects()
        dao.clearStudyTopics()
        dao.clearChatMessages()
        seedDemoData()
    }
}
