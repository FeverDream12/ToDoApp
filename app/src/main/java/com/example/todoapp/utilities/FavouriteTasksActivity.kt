package com.example.todoapp.utilities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.databinding.ActivityFavouriteTasksBinding
import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.example.todoapp.ui.home.TaskItem.TaskItemAdapter
import com.example.todoapp.ui.home.TaskItem.TaskItemClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FavouriteTasksActivity : AppCompatActivity(), TaskItemClickListener {

    private lateinit var binding: ActivityFavouriteTasksBinding

    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var taskList: ArrayList<TaskItem>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavouriteTasksBinding.inflate(layoutInflater)
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
                setRecycleView("")
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }
        })
    }

    private fun setRecycleView(searchQuery: String) {
        val activity = this

        binding.favTasksRecycleView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = TaskItemAdapter(favouriteList(searchQuery), activity)
        }
    }

    private fun favouriteList(searchQuery: String): List<TaskItem> {
        val favouriteList = arrayListOf<TaskItem>()

        taskList.forEach {
            if(it.isFavourite == "true"){
                favouriteList.add(it)
            }
        }

        return favouriteList
    }

    override fun editTaskItem(taskItem: TaskItem) {
        TODO("Not yet implemented")
    }

    override fun completeTaskItem(taskItem: TaskItem) {
        TODO("Not yet implemented")
    }

    override fun deleteTaskItem(taskItem: TaskItem) {
        TODO("Not yet implemented")
    }

    override fun setTaskFavorite(taskItem: TaskItem) {
        TODO("Not yet implemented")
    }
}