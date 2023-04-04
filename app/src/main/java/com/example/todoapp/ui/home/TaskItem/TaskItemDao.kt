package com.example.todoapp.ui.home.TaskItem

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.example.todoapp.ui.notes.NoteItem.NoteItem

@Dao
interface TaskItemDao {
    @Query("SELECT * FROM task_item_table ORDER BY id ASC")
    fun allTaskItems(): LiveData<List<TaskItem>>

    @Query("SELECT * FROM task_item_table WHERE status NOT LIKE 'done' ORDER BY id ASC")
    fun liveTaskItems(): LiveData<List<TaskItem>>

    @Query("SELECT * FROM task_item_table WHERE status LIKE 'done' ORDER BY id ASC")
    fun doneTaskItems(): LiveData<List<TaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskItem(taskItem: TaskItem)

    @Update
    suspend fun updateTaskItem(taskItem: TaskItem)

    @Delete
    suspend fun deleteTaskItem(taskItem: TaskItem)


    @Query("SELECT * FROM task_item_table WHERE dueDate LIKE :searchQuery AND status NOT LIKE 'done'")
    fun searchTaskItemByDate(searchQuery: String): LiveData<List<TaskItem>>

    @Query("SELECT * FROM task_item_table WHERE category LIKE :searchQuery AND status NOT LIKE 'done'")
    fun searchTaskItemByCategory(searchQuery: String): LiveData<List<TaskItem>>

}