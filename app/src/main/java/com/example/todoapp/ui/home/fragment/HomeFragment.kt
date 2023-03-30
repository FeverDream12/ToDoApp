package com.example.todoapp.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.*
import com.example.todoapp.ui.home.TaskItem.TaskItemClickListener
import com.example.todoapp.databinding.FragmentHomeBinding

import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.example.todoapp.ui.home.TaskItem.TaskItemModelFactory
import com.example.todoapp.ui.home.TaskItem.TaskViewModel
import com.example.todoapp.ui.home.TaskItem.TaskItemAdapter

class HomeFragment : Fragment(), TaskItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private val taskViewModel: TaskViewModel by viewModels {
        val application = requireContext()
        TaskItemModelFactory((application.applicationContext as TodoApplication).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.newTaskButton.setOnClickListener{
            NewTaskSheet(null).show(childFragmentManager, "newTaskTag")
        }
        setRecycleView()

        return binding.root
    }

    private fun setRecycleView() {
        val activity = this
        taskViewModel.liveTaskItems.observe(viewLifecycleOwner){
            binding.listRecycleView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = TaskItemAdapter(it, activity)
            }
        }
    }

    override fun editTaskItem(taskItem: TaskItem) {
        NewTaskSheet(taskItem).show(childFragmentManager, "editTaskTag")
    }

    override fun completeTaskItem(taskItem: TaskItem) {
        taskViewModel.setTaskCompleted(taskItem)
    }

    override fun deleteTaskItem(taskItem: TaskItem) {
        taskViewModel.deleteTaskItem(taskItem)
    }
}