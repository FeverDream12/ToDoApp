package com.example.todoapp.utilities

import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.databinding.ActivityStatisticBinding
import com.example.todoapp.mainMenu.home.TaskItem.*
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class StatisticActivity : AppCompatActivity(), TaskItemClickListener {

    private lateinit var binding: ActivityStatisticBinding

    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var taskList: ArrayList<TaskItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStatisticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("TaskItems").child(auth.currentUser?.uid.toString())
        taskList = arrayListOf()

        deleteSwipe()

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

                var doneCount = 0
                var doneInTimeCount = 0
                var liveCount = 0

                taskList?.forEach {
                    if(it.status != "live"){
                        doneCount++
                    }
                    if(it.status == "live"){
                        liveCount++
                    }
                    if(it.completedDateString!! <= it.dueDate!!){
                        doneInTimeCount++
                    }
                }

                binding.tasksCount.text =  taskList?.size.toString()

                binding.liveTasks.text = "Незавершенных задач: $liveCount"
                binding.doneInTimeTasks.text = "Задач выполнено вовремя: $doneInTimeCount"

                binding.tasksDoneCount.text = doneCount.toString()

                if (doneList().size == 0){
                    binding.doneTaskTitle.visibility = TextView.GONE
                }

                if (taskList?.size == 0){
                    binding.catTitle.visibility = TextView.GONE
                    binding.pieCard.visibility = CardView.GONE
                }

                pieChartInit()
                setDoneItemsRecycleView()
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }
        })

        binding.backStatButton.setOnClickListener{
            finish()
        }
    }

    private fun pieChartInit() {
        val pieList: ArrayList<PieEntry> = ArrayList()
        val categories: ArrayList<String> = ArrayList()

        taskList.forEach {
            if(!categories.contains(it.category.toString())){
                categories.add(it.category.toString())
            }
        }

        categories.forEach{
            var catCount = 0
            val curCat = it
            taskList.forEach{
                if(it.category == curCat){
                    catCount++
                }
            }
            pieList.add(PieEntry(catCount.toFloat(),curCat))
        }

        val pieDataset = PieDataSet(pieList,"")
        pieDataset.setColors(ColorTemplate.MATERIAL_COLORS,255)
        pieDataset.valueTextSize = 15f
        pieDataset.valueTextColor= Color.WHITE

        binding.pieChart.data = PieData(pieDataset)
        binding.pieChart.description.text = ""
        binding.pieChart.centerText = ""
        binding.pieChart.holeRadius = 22f
        binding.pieChart.transparentCircleRadius = 26f
        binding.pieChart.animateY(1500)
    }

    private fun setDoneItemsRecycleView() {
        val activity = this
        binding.statListRecycleView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TaskItemAdapter(doneList(), activity)
        }
    }

    private fun doneList() : ArrayList<TaskItem>{
        val filteredTaskList = arrayListOf<TaskItem>()

        taskList.forEach {
            if(it.status == "done"){
                filteredTaskList.add(it)
            }
        }

        return filteredTaskList
    }

    override fun editTaskItem(taskItem: TaskItem) {}

    override fun completeTaskItem(taskItem: TaskItem) {}

    override fun deleteTaskItem(taskItem: TaskItem) {
        databaseRef.child(taskItem.id.toString()).removeValue()
    }

    override fun setTaskFavorite(taskItem: TaskItem) {}

    override fun rescheduleTaskItem(taskItem: TaskItem, time: String) {}
    override fun copyTaskItem(taskItem: TaskItem) {}

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

                var item = doneList()[position]

                when (direction){
                    ItemTouchHelper.LEFT ->{
                        deleteTaskItem(item)

                        Snackbar.make(
                            binding.statAct,
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
                    ItemTouchHelper.RIGHT ->{
                        setTaskFavorite(item)
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
        }).attachToRecyclerView(binding.statListRecycleView)
    }
}