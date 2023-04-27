package com.example.todoapp.repeatedTasks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.R
import com.example.todoapp.databinding.ActivityFavouriteTasksBinding
import com.example.todoapp.databinding.ActivityRepeatedTasksBinding
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.itemSheets.NewRepeatedTaskSheet
import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.example.todoapp.ui.home.TaskItem.TaskItemAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.HashMap

class RepeatedTasksActivity : AppCompatActivity(), RepeatedTaskItemClickListener {

    private lateinit var binding: ActivityRepeatedTasksBinding
    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var repeatedTaskList: ArrayList<RepeatedTaskItem>
    private lateinit var selectedDay: LocalDate
    private lateinit var weekDay: String

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRepeatedTasksBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("RepeatedTaskItems").child(auth.currentUser?.uid.toString())
        repeatedTaskList = arrayListOf()
        selectedDay = LocalDate.now()


        updateView()

        binding.newRepeatedTaskButton.setOnClickListener{
            NewRepeatedTaskSheet(null).show(supportFragmentManager, "newTaskTag")
        }

        binding.prevArrRep.setOnClickListener{
            selectedDay = selectedDay.minusDays(1)
            updateView()
        }
        binding.nextArrRep.setOnClickListener{
            selectedDay = selectedDay.plusDays(1)
            updateView()
        }

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                repeatedTaskList.clear()
                if(snapshot.exists()){
                    snapshot.children.map{
                        val task : RepeatedTaskItem? = it.getValue(RepeatedTaskItem::class.java)
                        task!!.id = it.key
                        repeatedTaskList.add(task!!)
                    }
                }

                setRecycleView()
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }
        })


        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    private fun updateView() {
        binding.currentDayText.text = selectedDay.toString()+", " + getWeekDay(selectedDay)
        setRecycleView()
    }

    private fun getWeekDay(selectedDay: LocalDate): String {
        val day : Int = selectedDay.dayOfWeek.value
        var dayStr  = ""

        when (day) {
            1 -> dayStr = "Понедельник"
            2 -> dayStr = "Вторник"
            3 -> dayStr = "Среда"
            4 -> dayStr = "Четверг"
            5 -> dayStr = "Пятница"
            6 -> dayStr = "Суббота"
            7 -> dayStr = "Воскресенье"
        }

        return dayStr
    }

    private fun setRecycleView() {
        val activity = this

        binding.listRecycleView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = RepeatedTaskItemAdapter(selectedDayList(selectedDay.dayOfWeek.value),activity,selectedDay.toString())
        }
    }

    private fun selectedDayList(day: Int): List<RepeatedTaskItem> {
        val selectedDayList = arrayListOf<RepeatedTaskItem>()

        repeatedTaskList.forEach {
            if(it.daysOfWeek!!.contains(day)){
                selectedDayList.add(it)
            }
        }
        return selectedDayList
    }

    private fun filteredDays(daysOfWeek: ArrayList<String>,day: String) : ArrayList<String>{
        var buf = arrayListOf<String>()

        daysOfWeek.forEach{
            if(it != day){
                buf.add(it)
            }
        }
        return buf
    }


    override fun editRepeatedTaskItem(repeatedTaskItem: RepeatedTaskItem) {
        NewRepeatedTaskSheet(repeatedTaskItem).show(supportFragmentManager, "newTaskTag")
    }

    override fun completeRepeatedTaskItem(repeatedTaskItem: RepeatedTaskItem, date: String) {
        if(repeatedTaskItem.doneDays!!.contains(date)){
            repeatedTaskItem.doneDays = filteredDays(repeatedTaskItem.doneDays!!,selectedDay.toString())
        }else{
            repeatedTaskItem.doneDays!!.add(date)
        }
        updateItem(repeatedTaskItem)
    }

    override fun deleteTaskItem(repeatedTaskItem: RepeatedTaskItem) {
        //
    }

    private fun updateItem(repeatedTaskItem: RepeatedTaskItem) {
        val map = HashMap<String, Any>()
        map[repeatedTaskItem.id.toString()] = repeatedTaskItem
        databaseRef.updateChildren(map)
    }
}