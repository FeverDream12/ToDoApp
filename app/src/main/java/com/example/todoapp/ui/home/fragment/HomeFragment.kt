package com.example.todoapp.ui.home.fragment

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.*
import com.example.todoapp.ui.home.TaskItem.TaskItemClickListener
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.ui.home.Category.CategoryAdapter
import com.example.todoapp.ui.home.Category.CategoryItemClickListener
import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.example.todoapp.ui.home.TaskItem.TaskItemModelFactory
import com.example.todoapp.ui.home.TaskItem.TaskViewModel
import com.example.todoapp.ui.home.TaskItem.TaskItemAdapter

class HomeFragment : Fragment(), TaskItemClickListener, CategoryItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private val taskViewModel: TaskViewModel by viewModels {
        val application = requireContext()
        TaskItemModelFactory((application.applicationContext as TodoApplication).repository)
    }

    private var selectedCategory = "Все"
    private lateinit var categories : ArrayList<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.newTaskButton.setOnClickListener{
            NewTaskSheet(null).show(childFragmentManager, "newTaskTag")
        }

        setCategoriesList()
        setCategory(selectedCategory)

        return binding.root
    }

    private fun setCategoriesList() {
        categories = ArrayList<String>()
        categories.add("Все")
        categories.add("Без категории")
        categories.add("Работа")
        categories.add("Учеба")
        categories.add("Личное")


        val itemList: List<TaskItem>? = taskViewModel.liveTaskItems.value
        if(itemList!= null){
            val itemArray =  ArrayList(itemList)
            itemArray.forEach {
                if(categories.contains(it.category.toString())){

                }else{
                    categories.add(it.category.toString())
                }
            }
        }
    }

    private fun setCategoriesView(categories: ArrayList<String>) {
        val activity = this
        binding.categoriesRecycleView.apply {
            binding.categoriesRecycleView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
            adapter= CategoryAdapter(categories,activity,taskViewModel,selectedCategory)
        }
    }

    private fun cancelScheduledNotification(id: Int) {
        val intent = Intent(requireContext(), NotificationReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(requireContext(), id,intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)
    }

    private fun setRecycleView() {
        val activity = this

        Toast.makeText(context, selectedCategory + " rv", Toast.LENGTH_SHORT).show()

        if(selectedCategory == "Все"){

            taskViewModel.taskItems.observe(viewLifecycleOwner){}
            taskViewModel.liveTaskItems.observe(viewLifecycleOwner){
                binding.listRecycleView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = TaskItemAdapter(it, activity)
                }
                setCategoriesList()
                setCategoriesView(categories)
            }
        }else{

            taskViewModel.taskItems.observe(viewLifecycleOwner){}
            taskViewModel.searchTaskItemByCategory(selectedCategory).observe(viewLifecycleOwner){
                binding.listRecycleView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = TaskItemAdapter(it, activity)
                }
                setCategoriesList()
                setCategoriesView(categories)
            }
        }
    }

    override fun editTaskItem(taskItem: TaskItem) {
        NewTaskSheet(taskItem).show(childFragmentManager, "editTaskTag")
    }

    override fun completeTaskItem(taskItem: TaskItem) {
        taskViewModel.setTaskCompleted(taskItem)
        setRecycleView()
    }

    override fun deleteTaskItem(taskItem: TaskItem) {
        taskViewModel.deleteTaskItem(taskItem)
        if(taskItem.notificationId != null){
            cancelScheduledNotification(taskItem.notificationId!!)
        }
    }

    override fun setCategory(category: String) {
        selectedCategory = category
        Toast.makeText(context, category, Toast.LENGTH_SHORT).show()
        setCategoriesView(categories)
        setRecycleView()
    }
}