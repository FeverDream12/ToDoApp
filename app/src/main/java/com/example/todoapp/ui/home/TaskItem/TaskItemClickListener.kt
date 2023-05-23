package com.example.todoapp.ui.home.TaskItem

interface TaskItemClickListener {
    fun editTaskItem(taskItem: TaskItem)
    fun completeTaskItem(taskItem: TaskItem)
    fun deleteTaskItem(taskItem: TaskItem)
    fun setTaskFavorite(taskItem: TaskItem)
    fun rescheduleTaskItem(taskItem: TaskItem, time: String)
}