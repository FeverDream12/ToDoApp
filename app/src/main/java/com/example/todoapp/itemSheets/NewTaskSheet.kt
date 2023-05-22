package com.example.todoapp.itemSheets

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Toast
import com.example.todoapp.NotificationReceiver
import com.example.todoapp.R
import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.example.todoapp.databinding.FragmentNewTaskSheetBinding
import com.example.todoapp.messageExtra
import com.example.todoapp.titleExtra
import com.example.todoapp.ui.home.TaskItem.TaskItem.Companion.timeFormatter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random.Default.nextInt

class NewTaskSheet(var taskItem: TaskItem?,val taskList : ArrayList<TaskItem>, val dueSetDate: String?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewTaskSheetBinding
    private var dueTime: LocalTime? = null
    private var dueDate: String = LocalDate.now().toString()
    private var gotNot: Boolean = false
    private var taskCat = "Без категории"
    private var otherCat = true
    private var priorityStr = "Обычный"

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference


    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        val categories = ArrayList<String>()
        val priorityList = ArrayList<String>()

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("TaskItems").child(auth.currentUser?.uid.toString())

        categories.add("Без категории")
        categories.add("Работа")
        categories.add("Учеба")
        categories.add("Личное")

        priorityList.add("Обычный")
        priorityList.add("Средний")
        priorityList.add("Высокий")

        taskList.forEach {
            if(categories.contains(it.category.toString())){

            }else{
                categories.add(it.category.toString())
            }
        }

        categories.add("Другое")


        if(taskItem != null){
            priorityStr = getPriority(taskItem!!.priority.toString())
            binding.priorityMenu.setText(priorityStr)

            taskCat = taskItem!!.category!!
            binding.categoryMenu.setText(taskCat)
        }else{
            if(dueSetDate!= null){
                dueDate = dueSetDate
                binding.DueText.text = "Отложено на: " + dueDate
            }

            binding.priorityMenu.setText("Обычный")
            binding.categoryMenu.setText("Без категории")
        }


        val categoryAdapter = ArrayAdapter(requireContext(), R.layout.categorylist_item, categories)
        binding.categoryMenu.setAdapter(categoryAdapter)

        val priorityAdapter = ArrayAdapter(requireContext(), R.layout.prioritylist_item, priorityList)
        binding.priorityMenu.setAdapter(priorityAdapter)

        binding.categoryMenu.onItemClickListener = AdapterView.OnItemClickListener{
            adapterView, view, i, l ->
            val selectedItem = adapterView.getItemAtPosition(i)

            if (selectedItem == "Другое"){
                otherCat = true
                binding.myCategory.visibility = TextInputEditText.VISIBLE
            }else{
                taskCat = selectedItem.toString()
                otherCat = false
                binding.myCategory.visibility = TextInputEditText.GONE
            }
        }

        binding.priorityMenu.onItemClickListener = AdapterView.OnItemClickListener{
                adapterView, view, i, l ->
            priorityStr = adapterView.getItemAtPosition(i).toString()
        }


        if(taskItem != null){

            binding.taskTitle.text = "Изменить задачу"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(taskItem!!.name)
            binding.desc.text = editable.newEditable(taskItem!!.desc)

            if (taskItem!!.dueTimeString != "null"){
                dueTime = LocalTime.parse(taskItem!!.dueTimeString, timeFormatter)
            }
            if(taskItem!!.dueDate != "null"){
                dueDate = taskItem!!.dueDate!!
                binding.DueText.text = "Отложено на: " + taskItem!!.dueDate
            }
            if(taskItem!!.notificationId != 0){
                gotNot = true
            }
            if(taskItem!!.dueTimeString != "null"){
                binding.notifButton.visibility = ImageButton.VISIBLE
            }
        }else{
            binding.taskTitle.text = "Новая задача"
        }

        binding.saveButton.setOnClickListener{
            saveAction()
        }
        binding.timePickerButton.setOnClickListener{
            openTimePicker()
            binding.notifButton.visibility = ImageButton.VISIBLE
        }

        binding.notifButton.setOnClickListener{
            if(dueTime != null && dueDate != "null"){
                gotNot = !gotNot

                if(gotNot){
                    Toast.makeText(context, "Уведомление установленно", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "Уведомление отменено", Toast.LENGTH_SHORT).show()
                }

            }
        }

        val calendar = Calendar.getInstance()


        binding.dueDatePicker.setOnClickListener{

            val datePicker = DatePickerDialog.OnDateSetListener{ view, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR,year)
                calendar.set(Calendar.MONTH,month)
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)

                val myFormat = "yyyy-MM-dd"
                val simpleDateFormat = SimpleDateFormat(myFormat, Locale.KOREA)
                dueDate = simpleDateFormat.format(calendar.time)
            }

            DatePickerDialog(activity, datePicker, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) ).show()

        }
    }

    private fun getPriority(priority: String): String {
        var priorityStr = ""
        when(priority){
            "0" -> priorityStr = "Обычный"
            "1" -> priorityStr = "Средний"
            "2" -> priorityStr = "Высокий"
            "Обычный" -> priorityStr = "0"
            "Средний" -> priorityStr = "1"
            "Высокий" -> priorityStr = "2"
        }

        return  priorityStr
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

    private fun cancelScheduledNotification(id: Int) {
        val intent = Intent(requireContext(), NotificationReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(requireContext(), id,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)
    }


    private fun openTimePicker() {
        if(dueTime == null)
            dueTime = LocalTime.now()
        val listener = TimePickerDialog.OnTimeSetListener{_, selectedHour, selectedMinute ->
            dueTime = LocalTime.of(selectedHour,selectedMinute)
        }

        val dialog = TimePickerDialog(activity, listener, dueTime!!.hour,dueTime!!.minute, true)
        dialog.setTitle("Время")
        dialog.show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNewTaskSheetBinding.inflate( inflater,container, false)
        return binding.root
    }

    private fun updateItem(taskItem: TaskItem) {
        val map = HashMap<String, Any>()
        map[taskItem.id.toString()] = taskItem
        databaseRef.updateChildren(map)
    }

    private fun saveAction() {
        val name = binding.name.text.toString()
        val desc = binding.desc.text.toString()
        val dueTimeString = if(dueTime == null) "null" else TaskItem.timeFormatter.format(dueTime)

        if (otherCat && binding.myCategory.text.toString() != ""){
            taskCat = binding.myCategory.text.toString()
        }

        if(name != ""){
            if(taskItem == null)
            {
                val newTask : TaskItem
                if(dueDate == "null"){
                    newTask = TaskItem(name,desc,dueTimeString,"null", LocalDate.now().toString(),"live",0,taskCat,getPriority(priorityStr),"false")
                }
                else{
                    newTask = TaskItem(name,desc,dueTimeString,"null", dueDate,"live",0,taskCat,getPriority(priorityStr),"false")
                }

                if(gotNot){
                    newTask!!.notificationId = nextInt()
                    scheduleNotification(newTask)
                }

                val taskId = databaseRef.push().key!!

                databaseRef.child(taskId).setValue(newTask)

            }else
            {
                taskItem!!.name = name
                taskItem!!.desc = desc
                taskItem!!.category = taskCat
                taskItem!!.priority = getPriority(priorityStr)

                taskItem!!.dueTimeString = dueTimeString

                if(dueDate == "null"){
                    taskItem!!.dueDate = taskItem!!.dueDate
                }
                else{
                    taskItem!!.dueDate = dueDate
                }

                if(gotNot && taskItem!!.notificationId == 0) {
                    taskItem!!.notificationId = nextInt()
                    scheduleNotification(taskItem!!)
                }

                if(gotNot && taskItem!!.notificationId != 0) {
                    cancelScheduledNotification(taskItem!!.notificationId!!)
                    taskItem!!.notificationId = nextInt()
                    scheduleNotification(taskItem!!)
                }

                if(!gotNot && taskItem!!.notificationId != 0){
                    cancelScheduledNotification(taskItem!!.notificationId!!)
                    taskItem!!.notificationId = 0
                }

                updateItem(taskItem!!)
            }

            binding.name.setText("")
            binding.desc.setText("")

            dismiss()
        }
    }
}