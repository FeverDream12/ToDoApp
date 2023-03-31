package com.example.todoapp.ui.notes.NoteItem

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteItemDao {
    @Query("SELECT * FROM note_item_table ORDER BY id DESC")
    fun allNoteItems(): LiveData<List<NoteItem>>

    @Query("SELECT * FROM note_item_table WHERE isFavourite LIKE 'true' ORDER BY id DESC")
    fun favouriteNoteItems(): LiveData<List<NoteItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteItem(noteItem: NoteItem)

    @Update
    suspend fun updateNoteItem(noteItem: NoteItem)

    @Delete
    suspend fun deleteNoteItem(noteItem: NoteItem)

    @Query("SELECT * FROM note_item_table WHERE note LIKE '%' || :searchQuery || '%' or title LIKE '%' || :searchQuery || '%'")
    fun searchNoteItem(searchQuery: String): LiveData<List<NoteItem>>


}