package com.example.notes1.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.notes1.NoteDao
import com.example.notes1.model.Note

@Database(entities = [Note::class], version = 3 , exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {


    abstract fun noteDao(): NoteDao

    companion object {

        val migration_1to2=object : Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Notes ADD COLUMN isActive INTEGER NOT NULL DEFAULT 1")
            }
        }
        val migration_2to3=object : Migration(2,3){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Notes ADD COLUMN value1 INTEGER NOT NULL DEFAULT 1")
            }
        }

        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "notes_database"
                )
                    .addMigrations(migration_1to2,migration_2to3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}