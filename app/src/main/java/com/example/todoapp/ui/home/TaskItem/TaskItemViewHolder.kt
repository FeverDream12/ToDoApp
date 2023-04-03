package com.example.todoapp.ui.home.TaskItem

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.databinding.TaskItemCellBinding
import com.example.todoapp.ui.notes.NoteItem.NoteItem
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
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

        popupMenu(taskItem)

        if(taskItem.isCompleted()){
            binding.name.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.dueTime.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        if(lateCheck(taskItem) && taskItem.status == "live"){
            binding.lateDate.text = "Опиздун! " + taskItem.dueDate
            binding.date.text = ""
        }else{
            binding.date.text = taskItem.dueDate
            binding.lateDate.text = ""
        }

        if(LocalDate.parse(taskItem.dueDate) == LocalDate.now() && !lateCheck(taskItem)){
            binding.date.text = "Сегодня"
        }
        if(LocalDate.parse(taskItem.dueDate) == LocalDate.now().plusDays(1) && !lateCheck(taskItem)){
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



    fun lateCheck(taskItem: TaskItem) : Boolean{
        if(taskItem.dueTimeString != null){
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            return LocalDateTime.now().isAfter(LocalDateTime.parse(taskItem.dueDate + " " + taskItem.dueTimeString,formatter))
        }else{
            return LocalDate.now().isAfter(LocalDate.parse(taskItem.dueDate))
        }

    }

    private fun popupMenu(taskItem: TaskItem) {
        val popupMenu = PopupMenu(context,binding.taskCellContainer)
        popupMenu.inflate(R.menu.pop_up_menu)

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.delete_note ->{
                    clickListener.deleteTaskItem(taskItem)
                    true
                }
                else -> {
                    true
                }
            }
        }


        binding.taskCellContainer.setOnLongClickListener{
            try {
                val popup = PopupMenu::class.java.getDeclaredField("mpop")
                popup.isAccessible = true

                val menu = popup.get(popupMenu)
                menu.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(menu, true)
            } catch (e: Exception){
                e.printStackTrace()
            }finally {
                popupMenu.show()
            }
            true
        }
    }
}