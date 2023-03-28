package com.example.todoapp

import android.app.Application
import com.example.todoapp.ui.notes.NoteItem.NoteItemDatabase
import com.example.todoapp.ui.notes.NoteItem.NoteItemRepository
import com.example.todoapp.ui.home.TaskItem.TaskItemDatabase
import com.example.todoapp.ui.home.TaskItem.TaskItemRepository

class TodoApplication: Application()
{
    private val database by lazy { TaskItemDatabase.getDatabase(this) }
    val repository by lazy { TaskItemRepository(database.taskItemDao()) }

    private val notesDatabase by lazy { NoteItemDatabase.getDatabase(this) }
    val notesRepository by lazy { NoteItemRepository(notesDatabase.noteItemDao()) }
}