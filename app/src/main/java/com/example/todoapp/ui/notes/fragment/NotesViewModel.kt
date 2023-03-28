package com.example.todoapp.ui.notes.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.ui.notes.NoteItem.NoteItemRepository
import com.example.todoapp.ui.notes.NoteItem.NoteItem
import kotlinx.coroutines.launch

class NotesViewModel (private val repository: NoteItemRepository): ViewModel()  {

    var allNoteItems: LiveData<List<NoteItem>> = repository.allNoteItems

    fun addNoteItem(noteItem: NoteItem) = viewModelScope.launch {
        repository.insertNoteItem(noteItem)
    }

    fun updateNoteItem(noteItem: NoteItem) = viewModelScope.launch {
        repository.updateNoteItem(noteItem)
    }

    fun deleteNoteItem(noteItem: NoteItem) = viewModelScope.launch{
        repository.deleteNoteItem(noteItem)
    }

    fun searchNoteItem(searchQuery: String): LiveData<List<NoteItem>>{
        return repository.searchNoteItem(searchQuery)
    }
}