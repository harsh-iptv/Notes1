package com.example.notes1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.notes1.model.Note
import com.example.notes1.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class NotesViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: LiveData<String> = _searchQuery.asLiveData()

    val filteredNotes: LiveData<List<Note>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.getAllNotes()
            } else {
                repository.searchNotes("%$query%")
            }
        }
        .asLiveData()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addNote(note: Note) {
        viewModelScope.launch {
            repository.insertNote(note)
        }
    }

    fun deleteNote(noteId: Long) {
        viewModelScope.launch {
            val note = Note(id = noteId, title = "", content = "")
            repository.deleteNote(note)
        }
    }
}
