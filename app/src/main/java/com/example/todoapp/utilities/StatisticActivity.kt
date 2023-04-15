package com.example.todoapp.utilities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.databinding.ActivityStatisticBinding
import com.example.todoapp.ui.home.TaskItem.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

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

                var doneCount: Int = 0
                taskList?.forEach {
                    if(it.status != "live"){
                        doneCount++
                    }
                }

                binding.tasksCount.text =  taskList?.size.toString()
                binding.tasksDoneCount.text = doneCount.toString()

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

    override fun editTaskItem(taskItem: TaskItem) {
    }

    override fun completeTaskItem(taskItem: TaskItem) {
    }

    override fun deleteTaskItem(taskItem: TaskItem) {
        databaseRef.child(taskItem.id.toString()).removeValue()
    }
}