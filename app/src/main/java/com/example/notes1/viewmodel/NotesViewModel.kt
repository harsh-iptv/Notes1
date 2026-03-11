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


    private val _allNotes = MutableLiveData<MutableList<Note>>(
        savedStateHandle.get<ArrayList<Note>>(KEY_NOTES)?.toMutableList() ?: mutableListOf(

        )
    )


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
