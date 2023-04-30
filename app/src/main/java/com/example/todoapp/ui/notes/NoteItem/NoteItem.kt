package com.example.todoapp.ui.notes.NoteItem

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

class NoteItem(
    var title: String? = "null",
    var note: String? = "null",
    var date: String? = "null",
    var isFavourite: String? = "false",
    var id: String? = "null"
)
