package com.example.todoapp.ui.home.TaskItem

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
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

        if(taskItem.status == "completed"){
            binding.name.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.dueTime.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        //binding.favBtn.visibility = ImageButton.INVISIBLE
        if(taskItem.isFavourite == "true"){
            binding.favBtn.visibility = ImageButton.VISIBLE
        }else{
            binding.favBtn.visibility = ImageButton.INVISIBLE
        }

        binding.date.setTextColor(binding.dueTime.currentTextColor)
        if(lateCheck(taskItem) && taskItem.status == "live"){
            binding.date.setTextColor(ContextCompat.getColor(context,R.color.red))
        }else if (!lateCheck(taskItem) && taskItem.status == "live"){
            binding.date.text = taskItem.dueDate
        }else if(taskItem.status == "done"){
            binding.date.text = "Задача выполнена " + taskItem.completedDateString
        }

        if(LocalDate.parse(taskItem.dueDate) == LocalDate.now() && !lateCheck(taskItem) && taskItem.status != "done"){
            binding.date.text = "Сегодня"
        }
        if(LocalDate.parse(taskItem.dueDate) == LocalDate.now().plusDays(1) && !lateCheck(taskItem) && taskItem.status != "done"){
            binding.date.text = "Завтра"
        }

        binding.completeButton.setImageResource(taskItem.imageResource())
        binding.completeButton.setColorFilter(taskItem.imageColor(context))
        binding.name.setTextColor(taskItem.imageColor(context))

        if(taskItem.notificationId != 0){
            binding.notif.visibility = ImageView.VISIBLE
        }else{
            binding.notif.visibility = ImageView.INVISIBLE
        }

        binding.completeButton.setOnClickListener{
            clickListener.completeTaskItem(taskItem)
        }

        binding.taskCellContainer.setOnClickListener{
            clickListener.editTaskItem(taskItem)
        }

        if(taskItem.dueTime() != null){
            binding.dueTime.text = timeFormat.format(taskItem.dueTime())
        }else{
            binding.dueTime.text = ""
        }

        if(taskItem.priority == "0"){
            binding.completeButton.setColorFilter(ContextCompat.getColor(context,R.color.priority_0))
            binding.priorityView.setBackgroundColor(ContextCompat.getColor(context,R.color.priority_0))
        }else if(taskItem.priority == "1"){
            binding.completeButton.setColorFilter(ContextCompat.getColor(context,R.color.priority_1))
            binding.priorityView.setBackgroundColor(ContextCompat.getColor(context,R.color.priority_1))
        }else if(taskItem.priority == "2"){
            binding.completeButton.setColorFilter(ContextCompat.getColor(context,R.color.priority_2))
            binding.priorityView.setBackgroundColor(ContextCompat.getColor(context,R.color.priority_2))
        }

        if(taskItem.completedDateString != "null"){
            binding.completeButton.setImageResource(taskItem.imageResource())
            binding.completeButton.setColorFilter(taskItem.imageColor(context))
        }
    }



    fun lateCheck(taskItem: TaskItem) : Boolean{
        if(taskItem.dueTimeString != "null"){
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            return LocalDateTime.now().isAfter(LocalDateTime.parse(taskItem.dueDate + " " + taskItem.dueTimeString,formatter))
        }else{
            return LocalDate.now().isAfter(LocalDate.parse(taskItem.dueDate))
        }

    }

    private fun popupMenu(taskItem: TaskItem) {
        val popupMenu = PopupMenu(context,binding.taskCellContainer)
        popupMenu.inflate(R.menu.pop_up_task_menu)
        popupMenu.setForceShowIcon(true)

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.share ->{
                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "text/plane"
                    share.putExtra(Intent.EXTRA_TEXT,"Делюсь своей поставлленной задачей \"" + taskItem.name + "\"\n" + taskItem.desc + "\nВыполнить до: " + taskItem.dueDate)
                    val chooser = Intent.createChooser(share,"Поделиться через...")

                    context.startActivity(chooser)

                        //clickListener.deleteTaskItem(taskItem)
                    true
                }
                R.id.day ->{
                    clickListener.rescheduleTaskItem(taskItem,"day")
                    true
                }
                R.id.week ->{
                    clickListener.rescheduleTaskItem(taskItem,"week")
                    true
                }
                R.id.month ->{
                    clickListener.rescheduleTaskItem(taskItem,"month")
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