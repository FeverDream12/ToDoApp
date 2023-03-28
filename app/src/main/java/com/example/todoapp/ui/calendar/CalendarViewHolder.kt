package com.example.todoapp.ui.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.TodoApplication
import com.example.todoapp.databinding.CalendarItemCellBinding
import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.example.todoapp.ui.home.TaskItem.TaskItemAdapter
import com.example.todoapp.ui.home.TaskItem.TaskItemModelFactory
import com.example.todoapp.ui.home.TaskItem.TaskViewModel
import java.time.YearMonth
import java.time.format.DateTimeFormatter


class CalendarViewHolder(
    private val context: Context,
    private val binding: CalendarItemCellBinding,
    private val clickListener: CalendarItemClickListener
): RecyclerView.ViewHolder(binding.root){

    var dayMonth: TextView = itemView.findViewById(R.id.cellDayText)

    @SuppressLint("ResourceAsColor")
    fun bindCalendarItem(yearMonth: YearMonth, taskViewModel: TaskViewModel,selectedDay: String){
        //"" + yearMonth + "-" + binding.cellDayText.text.toString()

        var str: Int?
        if(binding.cellDayText.text.toString().length == 1){
            str = getTasksCount("" + yearMonth + "-0" + binding.cellDayText.text.toString(), taskViewModel)
        }else{
            str = getTasksCount("" + yearMonth + "-" + binding.cellDayText.text.toString(), taskViewModel)
        }

        if(binding.cellDayText.text == selectedDay){
            binding.calendarItem.setCardBackgroundColor(R.color.purple_500)
        }

        if(str != 0){
            binding.tasksCount.text = str.toString()
        }else{
            binding.tasksCount.text = ""
            binding.notCircle.isInvisible = true
        }

        binding.calendarItem.setOnClickListener(){
            clickListener.setDate(binding.cellDayText.text.toString())
        }
    }

    private fun getTasksCount(dateStr: String,taskViewModel: TaskViewModel) : Int? {

        val itemList: List<TaskItem> = taskViewModel.liveTaskItems.value!!
        val itemArray =  ArrayList<TaskItem>(itemList)
        var count : Int = 0
        itemArray.forEach {
            if(it.dueDate == dateStr){
                count++
            }
        }
        return count
    }
}