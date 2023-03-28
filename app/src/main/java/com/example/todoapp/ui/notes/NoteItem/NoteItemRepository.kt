package com.example.todoapp.ui.notes.NoteItem

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class NoteItemRepository(private val noteItemDao: NoteItemDao) {

    val allNoteItems: LiveData<List<NoteItem>> = noteItemDao.allNoteItems()

    @WorkerThread
    suspend fun insertNoteItem(noteItem: NoteItem){
        noteItemDao.insertNoteItem(noteItem)
    }

    @WorkerThread
    suspend fun updateNoteItem(noteItem: NoteItem){
        noteItemDao.updateNoteItem(noteItem)
    }

    @WorkerThread
    suspend fun deleteNoteItem(noteItem: NoteItem){
        noteItemDao.deleteNoteItem(noteItem)
    }

    @WorkerThread
    fun searchNoteItem(searchQuery: String): LiveData<List<NoteItem>>{
        return noteItemDao.searchNoteItem(searchQuery)
    }
}