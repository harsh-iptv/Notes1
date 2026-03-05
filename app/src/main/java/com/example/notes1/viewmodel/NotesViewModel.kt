package com.example.notes1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.notes1.model.Note

class NotesViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    companion object {
        private const val KEY_NOTES = "notes_list"
        private const val KEY_SEARCH_QUERY = "search_query"
    }

    // Backing list stored in SavedStateHandle for rotation survival
    private val _allNotes = MutableLiveData<MutableList<Note>>(
        savedStateHandle.get<ArrayList<Note>>(KEY_NOTES)?.toMutableList() ?: mutableListOf(
            Note(title = "Welcome to Notes!", content = "Tap the + button to create a new note. Long press a note to delete it."),
            Note(title = "Tips & Tricks", content = "• Swipe to refresh\n• Long press to delete\n• Tap to view details\n• Use the search bar to filter notes"),
            Note(title = "Sample Note", content = "This is a sample note to show how the app works. You can write anything here!")
        )
    )
    val allNotes: LiveData<MutableList<Note>> = _allNotes

    private val _filteredNotes = MutableLiveData<List<Note>>()
    val filteredNotes: LiveData<List<Note>> = _filteredNotes

    private val _searchQuery = MutableLiveData(
        savedStateHandle.get<String>(KEY_SEARCH_QUERY) ?: ""
    )
    val searchQuery: LiveData<String> = _searchQuery

    init {
        applyFilter()
    }

    fun addNote(note: Note) {
        val list = _allNotes.value ?: mutableListOf()
        list.add(0, note)
        _allNotes.value = list
        savedStateHandle[KEY_NOTES] = ArrayList(list)
        applyFilter()
    }

    fun deleteNote(noteId: Long) {
        val list = _allNotes.value ?: return
        list.removeAll { it.id == noteId }
        _allNotes.value = list
        savedStateHandle[KEY_NOTES] = ArrayList(list)
        applyFilter()
    }

    fun updateNote(updatedNote: Note) {
        val list = _allNotes.value ?: return
        val index = list.indexOfFirst { it.id == updatedNote.id }
        if (index != -1) {
            list[index] = updatedNote
            _allNotes.value = list
            savedStateHandle[KEY_NOTES] = ArrayList(list)
            applyFilter()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        savedStateHandle[KEY_SEARCH_QUERY] = query
        applyFilter()
    }

    private fun applyFilter() {
        val query = _searchQuery.value ?: ""
        val notes = _allNotes.value ?: emptyList()
        _filteredNotes.value = if (query.isBlank()) {
            notes.toList()
        } else {
            notes.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true)
            }
        }
    }

    fun getNoteCount(): Int = _allNotes.value?.size ?: 0
}
