package com.example.todoapp.itemSheets

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import com.example.todoapp.databinding.FragmentNewRepeatedTaskSheetBinding
import com.example.todoapp.repeatedTasks.RepeatedTaskItem
import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.LocalTime
import java.util.HashMap
import kotlin.random.Random

class NewRepeatedTaskSheet(var repeatedTask: RepeatedTaskItem?) : BottomSheetDialogFragment()  {

    private lateinit var binding: FragmentNewRepeatedTaskSheetBinding
    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var daysOfWeek : ArrayList<Int>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        daysOfWeek = arrayListOf()
        setWeekDays()
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("RepeatedTaskItems").child(auth.currentUser?.uid.toString())

        binding.selectAllDaysButton.setOnClickListener{
            selectAllDays()
        }
        binding.timePicker.setIs24HourView(true)

        binding.saveButton.setOnClickListener{
            if(binding.name.text.toString() != "" && daysOfWeek.size != 0)
                saveAction()
        }

        if(repeatedTask != null){

            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(repeatedTask!!.name)
            binding.desc.text = editable.newEditable(repeatedTask!!.desc)
            daysOfWeek = repeatedTask!!.daysOfWeek!!
            binding.timePicker.hour = LocalTime.parse(repeatedTask!!.dueTimeString!!).hour
            binding.timePicker.minute = LocalTime.parse(repeatedTask!!.dueTimeString!!).minute

            updateDays()
        }

    }

    private fun selectAllDays() {
        if(daysOfWeek.size != 7){
            daysOfWeek = arrayListOf()
            for (i in 1..7) {
                daysOfWeek.add(i)
            }
        }else{
            daysOfWeek = arrayListOf()
        }
        updateDays()
    }

    private fun updateDays() {
        dayOutlineCheck(1,binding.cardMondayOutline)
        dayOutlineCheck(2,binding.cardTuesdayOutline)
        dayOutlineCheck(3,binding.cardWednesdayOutline)
        dayOutlineCheck(4,binding.cardThursdayOutline)
        dayOutlineCheck(5,binding.cardFridayOutline)
        dayOutlineCheck(6,binding.cardSaturdayOutline)
        dayOutlineCheck(7,binding.cardSundayOutline)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewRepeatedTaskSheetBinding.inflate( inflater,container, false)
        return binding.root
    }

    private fun deleteDay(day: Int){
        var buf = arrayListOf<Int>()

        daysOfWeek.forEach{
            if(it != day){
                buf.add(it)
            }
        }
        daysOfWeek = buf
    }

    private fun dayOutlineCheck(day: Int, cardOutline: CardView){
        if(daysOfWeek.contains(day)){
            cardOutline.visibility = CardView.VISIBLE
        }else{
            cardOutline.visibility = CardView.INVISIBLE
        }
    }

    private fun dayClickCheck(day: Int, cardOutline: CardView){
        if(daysOfWeek.contains(day)){
            deleteDay(day)
        }else{
            daysOfWeek.add(day)
        }
        dayOutlineCheck(day, cardOutline)
    }

    private fun setWeekDays(){
        binding.cardMonday.setOnClickListener{
            dayClickCheck(1,binding.cardMondayOutline)
        }
        binding.cardTuesday.setOnClickListener{
            dayClickCheck(2,binding.cardTuesdayOutline)
        }
        binding.cardWednesday.setOnClickListener{
            dayClickCheck(3,binding.cardWednesdayOutline)
        }
        binding.cardThursday.setOnClickListener{
            dayClickCheck(4,binding.cardThursdayOutline)
        }
        binding.cardFriday.setOnClickListener{
            dayClickCheck(5,binding.cardFridayOutline)
        }
        binding.cardSaturday.setOnClickListener{
            dayClickCheck(6,binding.cardSaturdayOutline)
        }
        binding.cardSunday.setOnClickListener{
            dayClickCheck(7,binding.cardSundayOutline)
        }
    }

    private fun saveAction() {
        val name = binding.name.text.toString()
        val desc = binding.desc.text.toString()

        var dueTime : String
        var minuteStr : String
        var hourStr : String

        if (binding.timePicker.minute < 10){
            minuteStr = "0" + binding.timePicker.minute.toString()
        }else{
            minuteStr = binding.timePicker.minute.toString()
        }
        if (binding.timePicker.hour < 10){
            hourStr = "0" + binding.timePicker.hour.toString()
        }else{
            hourStr = binding.timePicker.hour.toString()
        }
        dueTime = "$hourStr:$minuteStr:00"

        if(repeatedTask == null){
            val newRepeatedTask = RepeatedTaskItem(name, desc,daysOfWeek, arrayListOf(),dueTime)

            val taskId = databaseRef.push().key!!

            databaseRef.child(taskId).setValue(newRepeatedTask)
        }else{
            repeatedTask!!.name = name
            repeatedTask!!.desc = desc
            repeatedTask!!.dueTimeString = dueTime
            repeatedTask!!.daysOfWeek = daysOfWeek

            updateItem(repeatedTask!!)
        }

        binding.name.setText("")
        binding.desc.setText("")

        dismiss()
    }

    private fun updateItem(repeatedTaskItem: RepeatedTaskItem) {
        val map = HashMap<String, Any>()
        map[repeatedTaskItem.id.toString()] = repeatedTaskItem
        databaseRef.updateChildren(map)
    }


}