package com.example.todoapp.ui.home.TaskItem

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.databinding.TaskItemCellBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TaskItemViewHolder(
    private val context: Context,
    private val binding: TaskItemCellBinding,
    private val clickListener: TaskItemClickListener
) :RecyclerView.ViewHolder(binding.root) {

    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

    @SuppressLint("ResourceAsColor")
    fun bindTaskItem(taskItem: TaskItem){
        binding.name.text = taskItem.name
        binding.date.text = taskItem.dueDate

        if(taskItem.isCompleted()){
            binding.name.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.dueTime.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        if(LocalDate.now().isAfter(LocalDate.parse(taskItem.dueDate)) && taskItem.status == "live"){
            binding.lateDate.text = "Опиздун! " + taskItem.dueDate
            binding.date.text = ""
        }else{
            binding.date.text = taskItem.dueDate
            binding.lateDate.text = ""
        }

        if(LocalDate.parse(taskItem.dueDate) == LocalDate.now()){
            binding.date.text = "Сегодня"
        }
        if(LocalDate.parse(taskItem.dueDate) == LocalDate.now().plusDays(1)){
            binding.date.text = "Завтра"
        }

        binding.completeButton.setImageResource(taskItem.imageResource())
        binding.completeButton.setColorFilter(taskItem.imageColor(context))
        binding.deleteButton.setColorFilter(taskItem.imageColor(context))
        binding.name.setTextColor(taskItem.imageColor(context))

        if(taskItem.notificationId != null){
            binding.notif.visibility = ImageView.VISIBLE
        }else{
            binding.notif.visibility = ImageView.INVISIBLE
        }


        binding.completeButton.setOnClickListener{
            clickListener.completeTaskItem(taskItem)
        }

        binding.deleteButton.setOnClickListener{
            clickListener.deleteTaskItem(taskItem)
        }

        binding.taskCellContainer.setOnClickListener{
            clickListener.editTaskItem(taskItem)
        }

        if(taskItem.dueTime() != null){
            binding.dueTime.text = timeFormat.format(taskItem.dueTime())
        }else{
            binding.dueTime.text = ""
        }
    }
}