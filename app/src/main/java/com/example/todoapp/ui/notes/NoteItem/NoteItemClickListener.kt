package com.example.todoapp.ui.notes.NoteItem

interface NoteItemClickListener {

    fun editNoteItem(noteItem: NoteItem)
    fun deleteNoteItem(noteItem: NoteItem)
}