package com.example.todoapp.ui.calendar.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.NotificationReceiver
import com.example.todoapp.itemSheets.NewTaskSheet
import com.example.todoapp.R
import com.example.todoapp.ui.calendar.CalendarAdapter
import com.example.todoapp.ui.calendar.CalendarItemClickListener
import com.example.todoapp.databinding.FragmentCalendarBinding
import com.example.todoapp.ui.home.TaskItem.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarFragment : Fragment(), CalendarItemClickListener, TaskItemClickListener {

    private lateinit var binding: FragmentCalendarBinding
    private lateinit var selectedDate: LocalDate
    private var weekView: Boolean = false
    private var selectedWeekDay: String = LocalDate.now().dayOfMonth.toString()
    private var selectedDay: String = LocalDate.now().dayOfMonth.toString()

    private lateinit var taskList: ArrayList<TaskItem>
    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        selectedDate = LocalDate.now()
        weekView = false

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("TaskItems").child(auth.currentUser?.uid.toString())
        taskList = arrayListOf<TaskItem>()

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                taskList.clear()
                if(snapshot.exists()){
                    snapshot.children.map{
                        val task : TaskItem? = it.getValue(TaskItem::class.java)
                        task!!.id = it.key
                        taskList.add(task!!)
                    }
                }
                setCalendarView()
                setDate(selectedDay)
                //setRecycleView(selectedDate.toString())
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })

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
        setDate(selectedWeekDay)

        return binding.root
    }

    private fun setCalendarView(){
        if(weekView){
            setWeekView(selectedWeekDay)
        }else{
            setMonthView()
        }
    }

    private fun setMonthView() {
        binding.calendarText.text = monthYearFromDate(selectedDate)
        val daysInMonth: ArrayList<String> = daysInMonthArray(selectedDate)

        val activity = this

        val yearMonth: YearMonth = YearMonth.from(selectedDate)

        binding.calendarView.apply {
            binding.calendarView.layoutManager = GridLayoutManager(context, 7)
            adapter= CalendarAdapter(daysInMonth, activity, yearMonth,selectedDay,taskList)
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
            adapter= CalendarAdapter(daysInWeek, activity, yearMonth,selectedDay,taskList)
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

            var count : Int = 0
            liveList().forEach {
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

    private fun liveList() : ArrayList<TaskItem>{
        val liveList = arrayListOf<TaskItem>()

        taskList.forEach {
            if(it.status != "done"){
                liveList.add(it)
            }
        }

        return liveList
    }


    private fun setRecycleView(date: String) {
        val activity = this

        binding.listRecycleView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TaskItemAdapter(dateFilteredList(date), activity)
        }
    }

    private fun dateFilteredList(date: String) : ArrayList<TaskItem>{
        val filteredTaskList = arrayListOf<TaskItem>()

        liveList().forEach {
            if(it.dueDate.toString() == date){
                filteredTaskList.add(it)
            }
        }

        return filteredTaskList
    }

    override fun editTaskItem(taskItem: TaskItem) {
        NewTaskSheet(taskItem,taskList).show(childFragmentManager, "editTaskTag")
    }

    override fun completeTaskItem(taskItem: TaskItem) {
        if(taskItem.status == "live"){
            taskItem.status = "completed"
            taskItem.completedDateString = LocalDate.now().toString()
        }else if(taskItem.status == "completed"){
            taskItem.status = "live"
            taskItem.completedDateString = "null"
        }
        updateItem(taskItem)
    }

    override fun deleteTaskItem(taskItem: TaskItem) {
        if(taskItem.status == "done" || taskItem.status == "live"){
            databaseRef.child(taskItem.id.toString()).removeValue()
        }else if (taskItem.status == "completed"){
            taskItem.status = "done"
            updateItem(taskItem)
        }
        if(taskItem.notificationId != null){
            cancelScheduledNotification(taskItem.notificationId!!)
        }
    }

    private fun updateItem(taskItem: TaskItem) {
        val map = HashMap<String, Any>()
        map[taskItem.id.toString()] = taskItem
        databaseRef.updateChildren(map)
    }

    private fun cancelScheduledNotification(id: Int) {
        val intent = Intent(requireContext(), NotificationReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(requireContext(), id,intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)
    }
}