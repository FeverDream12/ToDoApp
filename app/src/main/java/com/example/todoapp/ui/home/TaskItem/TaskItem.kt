package com.example.todoapp.ui.home.TaskItem

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.todoapp.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class TaskItem(
    var name: String?= "null",
    var desc: String?= "null",
    var dueTimeString: String?= "null",
    var completedDateString: String?= "null",
    var dueDate: String?= "null",
    var status: String?= "null",
    var notificationId: Int? = 0,
    var category: String? = "null",
    var isFavourite: String? = "false",
    var id: String? = "null"
){
    fun dueTime(): LocalTime? = if(dueTimeString == "null") null
        else LocalTime.parse(dueTimeString, timeFormatter)

    fun imageResource(): Int = if(completedDateString != "null") R.drawable.checked_24 else R.drawable.unchecked_24

    fun imageColor(context: Context) : Int = if(completedDateString != "null") checked(context) else unchecked(context)

    private fun checked(context: Context) = ContextCompat.getColor(context, R.color.checkedColor)

    private fun unchecked(context: Context) = ContextCompat.getColor(context, R.color.uncheckedColor2)

    companion object{
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_TIME
    }
}

