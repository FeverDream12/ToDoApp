package com.example.todoapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.example.todoapp.databinding.FragmentNewTaskSheetBinding
import com.example.todoapp.ui.home.TaskItem.TaskViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class NewTaskSheet(var taskItem: TaskItem?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewTaskSheetBinding
    private lateinit var taskViewModel: TaskViewModel
    private var dueTime: LocalTime? = null
    private var dueDate: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        if(taskItem != null){
            binding.taskTitle.text = "Изменить задачу"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(taskItem!!.name)
            binding.desc.text = editable.newEditable(taskItem!!.desc)
            if (taskItem!!.dueTime() != null){
                dueTime = taskItem!!.dueTime()!!
            }
            if(taskItem!!.dueDate != null){
                binding.DueText.text = "Отложено на: " + taskItem!!.dueDate
            }
        }else{
            binding.taskTitle.text = "Новая задача"
        }

        taskViewModel = ViewModelProvider(activity)[TaskViewModel::class.java]

        binding.saveButton.setOnClickListener{
            saveAction()
        }
        binding.timePickerButton.setOnClickListener{
            openTimePicker()
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

            DatePickerDialog(activity,datePicker, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) ).show()


        }

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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewTaskSheetBinding.inflate( inflater,container, false)
        return binding.root
    }

    private fun saveAction() {
        val name = binding.name.text.toString()
        val desc = binding.desc.text.toString()
        val dueTimeString = if(dueTime == null) null else TaskItem.timeFormatter.format(dueTime)

        if(name != ""){
            if(taskItem == null)
            {
                val newTask : TaskItem
                if(dueDate == null){
                    newTask = TaskItem(name,desc,dueTimeString,null, LocalDate.now().toString(),"live")
                }
                else{
                    newTask = TaskItem(name,desc,dueTimeString,null, dueDate,"live")
                }

                taskViewModel.addTaskItem(newTask)
            }else
            {
                taskItem!!.name = name
                taskItem!!.desc = desc

                taskItem!!.dueTimeString = dueTimeString

                if(dueDate == null){
                    taskItem!!.dueDate = taskItem!!.dueDate
                }
                else{
                    taskItem!!.dueDate = dueDate
                }

                taskViewModel.updateTaskItem(taskItem!!)
            }

            binding.name.setText("")
            binding.desc.setText("")
            dismiss()
        }
    }

}