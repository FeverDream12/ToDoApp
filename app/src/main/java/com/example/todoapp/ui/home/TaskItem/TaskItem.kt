package com.example.todoapp.ui.home.TaskItem

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoapp.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "task_item_table")
class TaskItem(
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "desc") var desc: String,
    @ColumnInfo(name = "dueTimeString") var dueTimeString: String?,
    @ColumnInfo(name = "completedDateString") var completedDateString: String?,
    @ColumnInfo(name = "dueDate") var dueDate: String?,
    @ColumnInfo(name = "status") var status: String,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
){

    fun completedDate(): LocalDate? = if(completedDateString == null) null
        else LocalDate.parse(completedDateString, dateFormatter)

    fun dueTime(): LocalTime? = if(dueTimeString == null) null
        else LocalTime.parse(dueTimeString, timeFormatter)


    fun imageResource(): Int = if(isCompleted()) R.drawable.checked_24 else R.drawable.unchecked_24

    fun isCompleted()= completedDate() != null

    fun imageColor(context: Context) : Int = if(isCompleted()) checked(context) else unchecked(context)

    private fun checked(context: Context) = ContextCompat.getColor(context, R.color.checkedColor)
    private fun unchecked(context: Context) = ContextCompat.getColor(context, R.color.uncheckedColor2)


    companion object{
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_TIME
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE
    }
}

