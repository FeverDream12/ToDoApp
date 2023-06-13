package com.example.todoapp.mainMenu.notes.NoteItem

interface NoteItemClickListener {

    fun editNoteItem(noteItem: NoteItem)
    fun deleteNoteItem(noteItem: NoteItem)
    fun setNoteFavorite(noteItem: NoteItem)
}