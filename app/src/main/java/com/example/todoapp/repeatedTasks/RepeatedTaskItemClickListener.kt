package com.example.todoapp.repeatedTasks


interface RepeatedTaskItemClickListener {
    fun editRepeatedTaskItem(repeatedTaskItem: RepeatedTaskItem)
    fun completeRepeatedTaskItem(repeatedTaskItem: RepeatedTaskItem, date: String)
    fun deleteTaskItem(repeatedTaskItem: RepeatedTaskItem)
}