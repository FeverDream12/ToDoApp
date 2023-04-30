package com.example.todoapp.repeatedTasks

import android.content.Context
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.RepeatedTaskItemCellBinding
import java.time.format.DateTimeFormatter


class RepeatedTaskItemViewHolder(
    private val context: Context,
    private val binding: RepeatedTaskItemCellBinding,
    private val repeatedClickListener: RepeatedTaskItemClickListener,
    private val currentDay : String
) : RecyclerView.ViewHolder(binding.root){

    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

    fun bindRepeatedTaskItem(repeatedTaskItem: RepeatedTaskItem){
        binding.name.text = repeatedTaskItem.name

        binding.dueTime.text = timeFormat.format(repeatedTaskItem.dueTime())


        if(repeatedTaskItem.doneDays!!.contains(currentDay)){
            binding.name.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.dueTime.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        binding.completeButton.setImageResource(repeatedTaskItem.imageResource(currentDay))
        binding.completeButton.setColorFilter(repeatedTaskItem.imageColor(context,currentDay))
        binding.name.setTextColor(repeatedTaskItem.imageColor(context,currentDay))

        binding.completeButton.setOnClickListener{
            repeatedClickListener.completeRepeatedTaskItem(repeatedTaskItem,currentDay)
        }

        binding.repeatedTaskCellContainer.setOnClickListener{
            repeatedClickListener.editRepeatedTaskItem(repeatedTaskItem)
        }
    }

}