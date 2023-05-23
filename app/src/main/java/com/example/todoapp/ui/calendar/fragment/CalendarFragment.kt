package com.example.todoapp.ui.calendar.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.NotificationReceiver
import com.example.todoapp.itemSheets.NewTaskSheet
import com.example.todoapp.R
import com.example.todoapp.ui.calendar.CalendarAdapter
import com.example.todoapp.ui.calendar.CalendarItemClickListener
import com.example.todoapp.databinding.FragmentCalendarBinding
import com.example.todoapp.messageExtra
import com.example.todoapp.titleExtra
import com.example.todoapp.ui.home.TaskItem.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarFragment : Fragment(), CalendarItemClickListener, TaskItemClickListener {

    private lateinit var binding: FragmentCalendarBinding
    private lateinit var selectedDate: LocalDate
    private lateinit var anim: Animation
    private lateinit var taskList: ArrayList<TaskItem>
    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private var weekView: Boolean = false
    private var loaded: Boolean = false
    private var selectedWeekDay: String = LocalDate.now().dayOfMonth.toString()
    private var selectedDay: String = LocalDate.now().dayOfMonth.toString()
    private var selectedDateString: String = LocalDate.now().toString()
    private var setDateString: String = LocalDate.now().toString()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        selectedDate = LocalDate.now()
        weekView = false

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("TaskItems").child(auth.currentUser?.uid.toString())
        taskList = arrayListOf<TaskItem>()

        deleteSwipe()

        anim = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation).animation

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
                if(!loaded)
                    loaded = true
                setCalendarView()
                setDate(selectedDay)
            }
            override fun onCancelled(error: DatabaseError) {
                //Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
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

        binding.newTaskButton.setOnClickListener{
            NewTaskSheet(null,taskList, setDateString).show(childFragmentManager, "newTaskTag")
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

        setMonthText()

        val daysInMonth: ArrayList<String> = daysInMonthArray(selectedDate)

        val activity = this

        val yearMonth: YearMonth = YearMonth.from(selectedDate)

        binding.calendarView.apply {
            binding.calendarView.layoutManager = GridLayoutManager(context, 7)
            adapter= CalendarAdapter(daysInMonth, activity, yearMonth,selectedDay,taskList)
        }
    }

    private fun setMonthText() {
        val month = selectedDate.monthValue
        val year = selectedDate.year

        var monthStr = ""

        when(month){
            1 -> monthStr = "Январь"
            2 -> monthStr = "Февраль"
            3 -> monthStr = "Март"
            4 -> monthStr = "Апрель"
            5 -> monthStr = "Май"
            6 -> monthStr = "Июнь"
            7 -> monthStr = "Июль"
            8 -> monthStr = "Август"
            9 -> monthStr = "Сентябрь"
            10 -> monthStr = "Октябрь"
            11 -> monthStr = "Ноябрь"
            12 -> monthStr = "Декабрь"
        }

        binding.calendarText.text = "$monthStr, $year"
    }

    private fun setWeekView(day: String) {

        setMonthText()

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
            if(!loaded){
                binding.listRecycleView.startAnimation(anim)
            }
            loaded = false
            selectedWeekDay = date
            selectedDay = date

            if(weekView){
                setWeekView(selectedWeekDay)
            }else{
                setMonthView()
            }

            var dateStr: String

            if(date.length == 1){
                dateStr= "" + YearMonth.from(selectedDate) + "-0" + date
            }else{
                dateStr= "" + YearMonth.from(selectedDate) + "-" + date
            }

            setDateString = dateStr

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
                selectedDateString = LocalDate.parse(dateStr).toString()
                binding.descText.text = "Задачи на " + dateStr + ":"
                setRecycleView(selectedDateString)
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
        NewTaskSheet(taskItem,taskList,null).show(childFragmentManager, "editTaskTag")
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

    private fun deleteSwipe(){
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                var item: TaskItem

                item = dateFilteredList(selectedDateString)[position]

                when (direction){
                    ItemTouchHelper.LEFT ->{
                        deleteTaskItem(item)

                        Snackbar.make(
                            binding.calendarFrag,
                            "Задача \"" + item.name + "\" удалена",
                            Snackbar.LENGTH_SHORT
                        ).apply {
                            setAction("Отмена"){

                                if(item.status == "done"){
                                    item.status = "completed"
                                    updateItem(item)
                                }else{
                                    val taskId = databaseRef.push().key!!
                                    databaseRef.child(taskId).setValue(item)
                                }

                                if(item.notificationId != 0){
                                    scheduleNotification(item)
                                }
                            }
                            show()
                        }

                    }
                    ItemTouchHelper.RIGHT ->{
                        setTaskFavorite(item)
                    }
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftActionIcon(R.drawable.delete_24)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(),R.color.red2))
                    .addSwipeLeftCornerRadius(1, 15F)
                    .addSwipeRightActionIcon(R.drawable.baseline_star_30)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(requireContext(),R.color.fav))
                    .addSwipeRightCornerRadius(1, 15F)
                    .create()
                    .decorate()

                super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)

            }
        }).attachToRecyclerView(binding.listRecycleView)
    }

    override fun deleteTaskItem(taskItem: TaskItem) {
        if(taskItem.status == "done" || taskItem.status == "live"){
            databaseRef.child(taskItem.id.toString()).removeValue()
        }else if (taskItem.status == "completed"){
            taskItem.status = "done"
            updateItem(taskItem)
        }
        if(taskItem.notificationId != 0){
            cancelScheduledNotification(taskItem.notificationId!!)
        }
    }

    override fun setTaskFavorite(taskItem: TaskItem) {
        if(taskItem.isFavourite == "false"){
            taskItem.isFavourite = "true"
        }else{
            taskItem.isFavourite = "false"
        }
        updateItem(taskItem)
    }

    override fun rescheduleTaskItem(taskItem: TaskItem, time: String) {
        when(time){
            "day" ->{
                taskItem.dueDate = LocalDate.parse(taskItem.dueDate).plusDays(1).toString()
                updateItem(taskItem)
            }
            "week" ->{
                taskItem.dueDate = LocalDate.parse(taskItem.dueDate).plusWeeks(1).toString()
                updateItem(taskItem)
            }
            "month" ->{
                taskItem.dueDate = LocalDate.parse(taskItem.dueDate).plusMonths(1).toString()
                updateItem(taskItem)
            }
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

    private fun scheduleNotification(task: TaskItem) {
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        val title = task.name
        val message = task.desc

        intent.putExtra(titleExtra,title)
        intent.putExtra(messageExtra,message)

        val pendingIntent = PendingIntent.getBroadcast(requireContext(),task.notificationId!!,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val df = SimpleDateFormat("yyyy-MM-dd-HH:mm")
        val timeStr : String = task.dueDate + "-" + task.dueTimeString
        val time : Long = df.parse(timeStr).time

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time,pendingIntent)
    }
}