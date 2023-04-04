package com.example.todoapp.ui.calendar.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.NewTaskSheet
import com.example.todoapp.R
import com.example.todoapp.TodoApplication
import com.example.todoapp.ui.calendar.CalendarAdapter
import com.example.todoapp.ui.calendar.CalendarItemClickListener
import com.example.todoapp.databinding.FragmentCalendarBinding
import com.example.todoapp.ui.home.TaskItem.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.days

class CalendarFragment : Fragment(), CalendarItemClickListener, TaskItemClickListener {

    private lateinit var binding: FragmentCalendarBinding
    private lateinit var selectedDate: LocalDate
    private var weekView: Boolean = false
    private var selectedWeekDay: String = LocalDate.now().dayOfMonth.toString()
    private var selectedDay: String = LocalDate.now().dayOfMonth.toString()

    private val taskViewModel: TaskViewModel by viewModels {
        val application = requireContext()
        TaskItemModelFactory((application.applicationContext as TodoApplication).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        selectedDate = LocalDate.now()
        weekView = false


        setDate(selectedWeekDay)

        binding.weekViewArr.setOnClickListener{
            if(weekView){
                setMonthView()
                weekView = false
                binding.weekViewArr.setImageResource(R.drawable.arrow_up_24)
            }else{
                setWeekView(selectedWeekDay)
                weekView = true
                binding.weekViewArr.setImageResource(R.drawable.arrow_down_24)
            }
        }

        binding.prevArr.setOnClickListener{
            prevMonthAction()
        }
        binding.nextArr.setOnClickListener{
            nextMonthAction()
        }

        setMonthView()

        return binding.root
    }
    private fun setMonthView() {
        binding.calendarText.text = monthYearFromDate(selectedDate)
        val daysInMonth: ArrayList<String> = daysInMonthArray(selectedDate)

        val activity = this

        val yearMonth: YearMonth = YearMonth.from(selectedDate)

        binding.calendarView.apply {
            binding.calendarView.layoutManager = GridLayoutManager(context, 7)
            adapter= CalendarAdapter(daysInMonth, activity, yearMonth, taskViewModel,selectedDay)
        }
    }

    private fun setWeekView(day: String) {
        binding.calendarText.text = monthYearFromDate(selectedDate)
        val daysInMonth: ArrayList<String> = daysInMonthArray(selectedDate)

        val daysInWeek: ArrayList<String> = daysInWeekArray(daysInMonth, day)

        val activity = this

        val yearMonth: YearMonth = YearMonth.from(selectedDate)

        binding.calendarView.apply {
            binding.calendarView.layoutManager = GridLayoutManager(context, 7)
            adapter= CalendarAdapter(daysInWeek, activity, yearMonth, taskViewModel,selectedDay)
        }
    }

    private fun daysInMonthArray(selectedDate: LocalDate): ArrayList<String> {
        val daysInMonthArray = ArrayList<String>()

        val yearMonth: YearMonth = YearMonth.from(selectedDate)
        val daysInMonth: Int = yearMonth.lengthOfMonth()

        val firstDayOfMonth: LocalDate = selectedDate.withDayOfMonth(1)
        val dayOfWeek: Int = firstDayOfMonth.getDayOfWeek().value

        for(i in 1..42){
            if(i==36 && (i < dayOfWeek || i > daysInMonth + dayOfWeek -1)){
                break
            }
            if(i < dayOfWeek || i > daysInMonth + dayOfWeek -1){
                daysInMonthArray.add("")
            }else{
                daysInMonthArray.add( (i - dayOfWeek + 1).toString())
            }
        }
        return daysInMonthArray
    }

    private fun daysInWeekArray(month :ArrayList<String>,day: String): ArrayList<String> {

        val daysInWeekArray = ArrayList<String>()

        val firstDayOfMonth: LocalDate = selectedDate.withDayOfMonth(1)
        val dayOfWeek: Int = firstDayOfMonth.getDayOfWeek().value

        var weekDay: Int = day.toInt() % 7 + dayOfWeek - 1
        if(weekDay % 7 != 0){
            weekDay = weekDay % 7
        }

        val firstWeekDay : Int = day.toInt() - weekDay + 1

        val yearMonth: YearMonth = YearMonth.from(selectedDate)
        val daysInMonth: Int = yearMonth.lengthOfMonth()

        for(i in 1..7){
            var dayInt: Int = firstWeekDay + i - 1

            if(dayInt <= 0 || dayInt > daysInMonth){
                daysInWeekArray.add("")
            }else{
                daysInWeekArray.add(dayInt.toString())
            }
        }
        return daysInWeekArray
    }

    private fun monthYearFromDate(date: LocalDate) : String{
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter).uppercase()
    }

    private fun prevMonthAction() {
        selectedDate = selectedDate.minusMonths(1)
        binding.weekViewArr.setImageResource(R.drawable.arrow_down_24)
        viewReset()
        if(selectedDate.month == LocalDate.now().month){
            setDate(LocalDate.now().dayOfMonth.toString())
        }
    }
    private fun nextMonthAction() {
        selectedDate = selectedDate.plusMonths(1)
        binding.weekViewArr.setImageResource(R.drawable.arrow_down_24)
        viewReset()
        if(selectedDate.month == LocalDate.now().month){
            setDate(LocalDate.now().dayOfMonth.toString())
        }
    }

    private fun viewReset(){
        selectedDay = "99"
        selectedWeekDay = "1"
        weekView = false
        setMonthView()
        setRecycleView("")
        binding.descText.text = ""
    }

    override fun setDate(date: String) {
        if(date != ""){
            selectedWeekDay = date
            selectedDay = date

            if(weekView){
                setWeekView(selectedWeekDay)
            }else{
                setMonthView()
            }

            var selectedDateStr : LocalDate
            var dateStr: String

            if(date.length == 1){
                dateStr= "" + YearMonth.from(selectedDate) + "-0" + date
            }else{
                dateStr= "" + YearMonth.from(selectedDate) + "-" + date
            }

            val itemList: List<TaskItem> = taskViewModel.liveTaskItems.value!!
            val itemArray =  ArrayList<TaskItem>(itemList)
            var count : Int = 0
            itemArray.forEach {
                if(it.dueDate == dateStr){
                    count++
                }
            }

            if(count == 0){
                binding.descText.text = "Нет поставленных задач на \n" + dateStr
                setRecycleView("")
            }else{
                selectedDateStr = LocalDate.parse(dateStr)
                binding.descText.text = "Задачи на " + dateStr + ":"
                setRecycleView(selectedDateStr.toString())
            }
        }
    }

    private fun setRecycleView(toString: String) {
        val activity = this

        taskViewModel.taskItems.observe(viewLifecycleOwner){}

        taskViewModel.searchTaskItemByDate(toString).observe(viewLifecycleOwner){
            binding.listRecycleView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = TaskItemAdapter(it, activity)
            }
            setMonthView()
        }
    }

    override fun editTaskItem(taskItem: TaskItem) {
        NewTaskSheet(taskItem).show(childFragmentManager, "editTaskTag")
    }

    override fun completeTaskItem(taskItem: TaskItem) {
        taskViewModel.setTaskCompleted(taskItem)
    }

    override fun deleteTaskItem(taskItem: TaskItem) {
        taskViewModel.deleteTaskItem(taskItem)
        setMonthView()
    }


}