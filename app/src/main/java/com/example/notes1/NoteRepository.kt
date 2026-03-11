package com.example.notes1.repository

import com.example.notes1.NoteDao
import com.example.notes1.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {


    fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes()
    }

    fun searchNotes(query: String): Flow<List<Note>> {
        return noteDao.searchNotes(query)
    }


    suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }
}


