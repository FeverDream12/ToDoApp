package com.example.todoapp.ui.home.TaskItem

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskViewModel(private val repository: TaskItemRepository): ViewModel() {
    var taskItems: LiveData<List<TaskItem>> = repository.allTaskItems
    var liveTaskItems: LiveData<List<TaskItem>> = repository.liveTaskItems
    var doneTaskItems: LiveData<List<TaskItem>> = repository.doneTaskItems

    fun addTaskItem(newTask: TaskItem) = viewModelScope.launch {
        repository.insertTaskItem(newTask)
    }

    fun updateTaskItem(taskItem: TaskItem) = viewModelScope.launch {
        repository.updateTaskItem(taskItem)
    }

    fun setTaskCompleted(taskItem: TaskItem) = viewModelScope.launch {
        if(taskItem.status != "completed")
        {
            taskItem.completedDateString = TaskItem.dateFormatter.format(LocalDate.now())
            taskItem.status = "completed"
        }
        else
        {
            taskItem.completedDateString = null
            taskItem.status = "live"
        }
        repository.updateTaskItem(taskItem)
    }

    fun deleteTaskItem(taskItem: TaskItem) = viewModelScope.launch{
        if(taskItem.status == "live" || taskItem.status == "done")
        {
            repository.deleteTaskItem(taskItem)
        }else if(taskItem.status == "completed")
        {
            taskItem.status = "done"
            repository.updateTaskItem(taskItem)
        }

    }

    fun searchTaskItemByDate(searchQuery: String): LiveData<List<TaskItem>> {
        return repository.searchTaskItemByDate(searchQuery)
    }
}

class TaskItemModelFactory(private val repository: TaskItemRepository): ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {

        if(modelClass.isAssignableFrom(TaskViewModel::class.java))
            return TaskViewModel(repository) as T

        throw IllegalArgumentException("Unknown class for view model")
    }
}