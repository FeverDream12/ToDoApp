package com.example.todoapp.utilities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.databinding.ActivityStatisticBinding
import com.example.todoapp.ui.home.TaskItem.*

class StatisticActivity : AppCompatActivity(), TaskItemClickListener {

    private lateinit var binding: ActivityStatisticBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStatisticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setDoneItemsRecycleView()

        //val taskList : List<TaskItem>? = taskViewModel.taskItems.value

//        var doneCount: Int = 0
//        taskList?.forEach {
//            if(it.status == "done"){
//                doneCount++
//            }
//        }
//
//        binding.allTaskState.text = "Всего поставленно задач: " + taskList?.size.toString()
//        binding.doneTaskState.text = "Всего выполненно задач: " + doneCount.toString()

        binding.backStatButton.setOnClickListener{
            finish()
        }
    }

    private fun setDoneItemsRecycleView() {
//        val activity = this
//        taskViewModel.doneTaskItems.observe(this){
//            binding.statListRecycleView.apply {
//                layoutManager = LinearLayoutManager(applicationContext)
//                adapter = TaskItemAdapter(it, activity)
//            }
//        }
    }

    override fun editTaskItem(taskItem: TaskItem) {
    }

    override fun completeTaskItem(taskItem: TaskItem) {
    }

    override fun deleteTaskItem(taskItem: TaskItem) {
        //taskViewModel.deleteTaskItem(taskItem)
    }
}