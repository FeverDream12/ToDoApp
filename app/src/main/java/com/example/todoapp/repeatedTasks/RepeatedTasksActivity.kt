package com.example.todoapp.repeatedTasks

import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.databinding.ActivityRepeatedTasksBinding
import com.example.todoapp.itemSheets.NewRepeatedTaskSheet
import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.time.LocalDate
import java.util.HashMap

class RepeatedTasksActivity : AppCompatActivity(), RepeatedTaskItemClickListener {

    private lateinit var binding: ActivityRepeatedTasksBinding
    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var repeatedTaskList: ArrayList<RepeatedTaskItem>
    private lateinit var selectedDay: LocalDate
    private lateinit var anim: Animation
    private lateinit var animRight: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepeatedTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("RepeatedTaskItems").child(auth.currentUser?.uid.toString())
        repeatedTaskList = arrayListOf()
        selectedDay = LocalDate.now()

        anim = AnimationUtils.loadLayoutAnimation(this,R.anim.layout_animation).animation
        animRight = AnimationUtils.loadLayoutAnimation(this,R.anim.layout_animation_right).animation
        binding.listRecycleView.startAnimation(anim)


        updateView()

        binding.backRepButton.setOnClickListener{
            finish()
        }

        binding.newRepeatedTaskButton.setOnClickListener{
            NewRepeatedTaskSheet(null).show(supportFragmentManager, "newTaskTag")
        }

        binding.prevArrRep.setOnClickListener{
            selectedDay = selectedDay.minusDays(1)
            updateView()
            binding.listRecycleView.startAnimation(anim)
        }
        binding.nextArrRep.setOnClickListener{
            selectedDay = selectedDay.plusDays(1)
            updateView()
            binding.listRecycleView.startAnimation(animRight)
        }

        deleteSwipe()

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
                repeatedTaskList.sortBy { it.dueTimeString}

                setRecycleView()
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }
        })

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
        databaseRef.child(repeatedTaskItem.id.toString()).removeValue()
    }

    private fun updateItem(repeatedTaskItem: RepeatedTaskItem) {
        val map = HashMap<String, Any>()
        map[repeatedTaskItem.id.toString()] = repeatedTaskItem
        databaseRef.updateChildren(map)
    }

    private fun deleteSwipe(){
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
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

                val item = repeatedTaskList[position]

                when (direction){
                    ItemTouchHelper.LEFT ->{
                        deleteTaskItem(item)

                        Snackbar.make(
                            binding.root,
                            "Задача \"" + item.name + "\" удалена",
                            Snackbar.LENGTH_SHORT
                        ).apply {
                            setAction("Отмена"){

                                val taskId = databaseRef.push().key!!
                                databaseRef.child(taskId).setValue(item)
                            }
                            show()
                        }

                    }
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftActionIcon(R.drawable.delete_24)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(applicationContext, R.color.red2))
                    .addSwipeLeftCornerRadius(1, 15F)
                    .create()
                    .decorate()

                super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)

            }
        }).attachToRecyclerView(binding.listRecycleView)
    }
}