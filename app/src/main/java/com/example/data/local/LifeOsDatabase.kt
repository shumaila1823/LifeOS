package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        TaskEntity::class,
        SubtaskEntity::class,
        ProjectEntity::class,
        GoalEntity::class,
        GoalMilestoneEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        CalendarEventEntity::class,
        NoteEntity::class,
        StudySubjectEntity::class,
        StudyTopicEntity::class,
        FlashcardEntity::class,
        QuizQuestionEntity::class,
        AiMemoryEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LifeOsDatabase : RoomDatabase() {
    abstract fun dao(): LifeOsDao

    companion object {
        @Volatile
        private var INSTANCE: LifeOsDatabase? = null

        fun getDatabase(context: Context): LifeOsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeOsDatabase::class.java,
                    "lifeos_database"
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
