package com.example.todoapp.ui.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.databinding.CalendarItemCellBinding
import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.YearMonth


class CalendarViewHolder(
    private val context: Context,
    private val binding: CalendarItemCellBinding,
    private val clickListener: CalendarItemClickListener,
    private val taskList : ArrayList<TaskItem>
): RecyclerView.ViewHolder(binding.root){

    var dayMonth: TextView = itemView.findViewById(R.id.cellDayText)
    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    @SuppressLint("ResourceAsColor")
    fun bindCalendarItem(yearMonth: YearMonth, selectedDay: String){


        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("TaskItems").child(auth.currentUser?.uid.toString())

        var str: Int?
        if(binding.cellDayText.text.toString().length == 1){
            str = getTasksCount("" + yearMonth + "-0" + binding.cellDayText.text.toString())
        }else{
            str = getTasksCount("" + yearMonth + "-" + binding.cellDayText.text.toString())
        }

        if(binding.cellDayText.text == selectedDay){
            binding.outline.visibility = CardView.VISIBLE
        }else{
            binding.outline.visibility = CardView.INVISIBLE
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

    private fun liveList() : ArrayList<TaskItem>{
        val filteredTaskList = arrayListOf<TaskItem>()

        taskList.forEach {
            if(it.status != "done"){
                filteredTaskList.add(it)
            }
        }

        return filteredTaskList
    }

    private fun getTasksCount(dateStr: String) : Int? {

        var count : Int = 0
        liveList().forEach {
            if(it.dueDate == dateStr){
                count++
            }
        }
        return count
    }
}