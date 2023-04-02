package com.example.todoapp.ui.home.TaskItem

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.Query
import com.example.todoapp.ui.notes.NoteItem.NoteItem

class TaskItemRepository(private val taskItemDao: TaskItemDao) {

    val allTaskItems: LiveData<List<TaskItem>> = taskItemDao.allTaskItems()
    val liveTaskItems: LiveData<List<TaskItem>> = taskItemDao.liveTaskItems()
    val doneTaskItems: LiveData<List<TaskItem>> = taskItemDao.doneTaskItems()

    @WorkerThread
    suspend fun insertTaskItem(taskItem: TaskItem){
        taskItemDao.insertTaskItem(taskItem)
    }

    @WorkerThread
    suspend fun updateTaskItem(taskItem: TaskItem){
        taskItemDao.updateTaskItem(taskItem)
    }

    @WorkerThread
    suspend fun deleteTaskItem(taskItem: TaskItem){
        taskItemDao.deleteTaskItem(taskItem)
    }

    @WorkerThread
    fun searchTaskItemByDate(searchQuery: String): LiveData<List<TaskItem>> {
        return taskItemDao.searchTaskItemByDate(searchQuery)
    }
}