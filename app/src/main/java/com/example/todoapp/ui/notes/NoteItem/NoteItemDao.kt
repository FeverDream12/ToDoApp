package com.example.todoapp.ui.notes.NoteItem

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteItemDao {
    @Query("SELECT * FROM note_item_table ORDER BY id DESC")
    fun allNoteItems(): LiveData<List<NoteItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteItem(noteItem: NoteItem)

    @Update
    suspend fun updateNoteItem(noteItem: NoteItem)

//    @Query("UPDATE note_item_table Set title = :title, note = :note WHERE id = :id")
//    suspend fun updateNoteItem(id: Int?, title: String?, note: String?)

    @Delete
    suspend fun deleteNoteItem(noteItem: NoteItem)

    @Query("SELECT * FROM note_item_table WHERE note LIKE '%' || :searchQuery || '%' or title LIKE '%' || :searchQuery || '%'")
    fun searchNoteItem(searchQuery: String): LiveData<List<NoteItem>>


}