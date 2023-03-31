package com.example.todoapp.ui.notes.NoteItem

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteItemRepository): ViewModel() {
    var noteItems: LiveData<List<NoteItem>> = repository.allNoteItems

    fun favouriteNoteItems(): LiveData<List<NoteItem>> {
        return repository.favouriteNoteItems()
    }

    fun addNoteItem(newNote: NoteItem) = viewModelScope.launch {
        repository.insertNoteItem(newNote)
    }

    fun updateNoteItem(noteItem: NoteItem) = viewModelScope.launch {
        repository.updateNoteItem(noteItem)
    }

    fun deleteNoteItem(noteItem: NoteItem) = viewModelScope.launch{
        repository.deleteNoteItem(noteItem)
    }

    fun searchNoteItem(searchQuery: String): LiveData<List<NoteItem>> {
        return repository.searchNoteItem(searchQuery)
    }
}

class NoteItemModelFactory(private val repository: NoteItemRepository): ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if(modelClass.isAssignableFrom(NoteViewModel::class.java))
            return NoteViewModel(repository) as T

        throw IllegalArgumentException("Unknown class for view model")
    }
}