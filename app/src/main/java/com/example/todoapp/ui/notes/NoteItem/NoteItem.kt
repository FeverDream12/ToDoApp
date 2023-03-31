package com.example.todoapp.ui.notes.NoteItem

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_item_table")
class NoteItem(
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "note") var note: String,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "isFavourite") var isFavourite: String,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)
