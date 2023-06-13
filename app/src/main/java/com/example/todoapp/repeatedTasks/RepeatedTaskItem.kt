package com.example.todoapp.repeatedTasks

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.todoapp.R
import com.example.todoapp.mainMenu.home.TaskItem.TaskItem
import java.time.LocalTime

class RepeatedTaskItem(
    var name: String?= "null",
    var desc: String?= "null",
    var daysOfWeek : ArrayList<Int>? = arrayListOf(),
    var doneDays : ArrayList<String>? = arrayListOf(),
    var dueTimeString: String?= "null",
    var id: String? = "null"
) {
    fun dueTime(): LocalTime? = LocalTime.parse(dueTimeString, TaskItem.timeFormatter)

    fun imageResource(day : String): Int = if(doneDays!!.contains(day)) R.drawable.checked_24 else R.drawable.unchecked_24

    fun imageColor(context: Context,day : String) : Int = if(doneDays!!.contains(day)) checked(context) else unchecked(context)

    private fun checked(context: Context) = ContextCompat.getColor(context, R.color.checkedColor)

    private fun unchecked(context: Context) = ContextCompat.getColor(context, R.color.uncheckedColor2)
}