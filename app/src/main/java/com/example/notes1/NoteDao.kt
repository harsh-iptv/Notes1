package com.example.notes1

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.notes1.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
        @Query("SELECT * FROM Notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM Notes WHERE title LIKE :query OR content LIKE :query ORDER BY timestamp DESC")
    fun searchNotes(query: String): Flow<List<Note>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}