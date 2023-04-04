package com.example.todoapp.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.CalendarItemCellBinding
import com.example.todoapp.ui.home.TaskItem.TaskViewModel
import java.time.YearMonth

class CalendarAdapter(
    private val daysInMonth: ArrayList<String>,
    private val clickListener: CalendarItemClickListener,
    private val yearMonth: YearMonth,
    private val taskViewModel: TaskViewModel,
    private val selectedDay: String
): RecyclerView.Adapter<CalendarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {

        val from = LayoutInflater.from(parent.context)
        val binding = CalendarItemCellBinding.inflate(from, parent, false)
        return CalendarViewHolder(parent.context, binding, clickListener)
    }

    override fun getItemCount(): Int {
        return daysInMonth.size
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.dayMonth.text = daysInMonth[position]
        holder.bindCalendarItem(yearMonth, taskViewModel,selectedDay)
    }

}