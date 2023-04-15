package com.example.todoapp.ui.home.fragment

import com.example.todoapp.R
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.*
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.itemSheets.NewTaskSheet
import com.example.todoapp.ui.home.Category.CategoryAdapter
import com.example.todoapp.ui.home.Category.CategoryItemClickListener
import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.example.todoapp.ui.home.TaskItem.TaskItemAdapter
import com.example.todoapp.ui.home.TaskItem.TaskItemClickListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.text.SimpleDateFormat
import java.time.LocalDate


class HomeFragment : Fragment(), TaskItemClickListener, CategoryItemClickListener {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var taskList: ArrayList<TaskItem>
    private lateinit var taskItemsAdapter: TaskItemAdapter

    private var selectedCategory = "Все"
    private var sortKey = "id"
    private var sortOrder = "asc"
    private lateinit var categories : ArrayList<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("TaskItems").child(auth.currentUser?.uid.toString())
        taskList = arrayListOf<TaskItem>()
        taskItemsAdapter = TaskItemAdapter(taskList,this)

        binding.newTaskButton.setOnClickListener{
            NewTaskSheet(null,taskList).show(childFragmentManager, "newTaskTag")
        }

        deleteSwipe()
        popupMenu()

        databaseRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                taskList.clear()
                if(snapshot.exists()){
                    snapshot.children.map{
                        val task : TaskItem? = it.getValue(TaskItem::class.java)
                        task!!.id = it.key
                        taskList.add(task!!)
                    }
                }
                when(sortKey){
                    "id" ->{
                        taskList.sortBy { it.id}
                    }
                    "name" ->{
                        taskList.sortBy { it.name}
                    }
                    "dueDate" ->{
                        taskList.sortBy { it.dueDate}
                    }
                }
                if(sortOrder == "desc"){
                    taskList.reverse()
                }
                setCategoriesList()
                setCategory(selectedCategory)
                setRecycleView()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })

        return binding.root
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

                var item: TaskItem

                if (selectedCategory == "Все"){
                    item = liveList()[position]
                }else{
                    item = filteredList(selectedCategory)[position]
                }

                when (direction){
                    ItemTouchHelper.LEFT ->{
                        deleteTaskItem(item)

                        Snackbar.make(
                            binding.homeFrag,
                            "Задача \"" + item.name + "\" удалена",
                            Snackbar.LENGTH_SHORT
                        ).apply {
                            setAction("Отмена"){

                                if(item.status == "done"){
                                    item.status = "completed"
                                    updateItem(item)
                                }else{
                                    val taskId = databaseRef.push().key!!
                                    databaseRef.child(taskId).setValue(item)
                                }

                                if(item.notificationId != 0){
                                    scheduleNotification(item)
                                }
                            }
                            show()
                        }

                    }
                    ItemTouchHelper.RIGHT ->{

                    }
                }
            }

            override fun onChildDraw(c: Canvas,recyclerView: RecyclerView,viewHolder: RecyclerView.ViewHolder,dX: Float,dY: Float,actionState: Int,isCurrentlyActive: Boolean) {

                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftActionIcon(R.drawable.delete_24)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(),R.color.red2))
                    .create()
                    .decorate()

                super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)

            }
        }).attachToRecyclerView(binding.listRecycleView)
    }

    private fun refreshList(){
        if(taskList.size != 0){
            var item = taskList[0]
            val name = item.name

            item.name ="a"
            updateItem(item)

            item.name = name
            updateItem(item)
        }
    }

    private fun popupMenu() {
        val popupMenu = PopupMenu(context,binding.menuDots)
        popupMenu.inflate(R.menu.home_menu)

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.id_sort ->{
                    sortKey = "id"
                    sortOrder = "asc"
                    refreshList()
                    true
                }
                R.id.date_sort_asc ->{
                    sortKey = "dueDate"
                    sortOrder = "asc"
                    refreshList()
                    true
                }
                R.id.date_sort_desc ->{
                    sortKey = "dueDate"
                    sortOrder = "desc"
                    refreshList()
                    true
                }
                R.id.name_sort_asc ->{
                    sortKey = "name"
                    sortOrder = "asc"
                    refreshList()
                    true
                }
                R.id.name_sort_desc ->{
                    sortKey = "name"
                    sortOrder = "desc"
                    refreshList()
                    true
                }
                R.id.hideCompleteTasks ->{
                    liveList().forEach {
                        if(it.status == "completed"){
                            deleteTaskItem(it)
                        }
                    }
                    true
                }
                else -> {
                    true
                }
            }
        }


        binding.menuDots.setOnClickListener{
            try {
                val popup = PopupMenu::class.java.getDeclaredField("mpop")
                popup.isAccessible = true

                val menu = popup.get(popupMenu)
                menu.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(menu, true)
            } catch (e: Exception){
                e.printStackTrace()
            }finally {
                popupMenu.show()
            }
            true
        }
    }

    private fun setCategoriesList() {
        categories = ArrayList<String>()
        categories.add("Все")
        categories.add("Без категории")
        categories.add("Работа")
        categories.add("Учеба")
        categories.add("Личное")

        val itemArray =  ArrayList(taskList)
        itemArray.forEach {
            if(categories.contains(it.category.toString())){

            }else{
                categories.add(it.category.toString())
            }
        }
    }

    private fun setCategoriesView(categories: ArrayList<String>) {
        val activity = this
        binding.categoriesRecycleView.apply {
            binding.categoriesRecycleView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
            adapter= CategoryAdapter(categories,activity,selectedCategory)
        }
    }

    private fun scheduleNotification(task: TaskItem) {
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        val title = task.name
        val message = task.desc

        intent.putExtra(titleExtra,title)
        intent.putExtra(messageExtra,message)

        val pendingIntent = PendingIntent.getBroadcast(requireContext(),task.notificationId!!,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val df = SimpleDateFormat("yyyy-MM-dd-HH:mm")
        val timeStr : String = task.dueDate + "-" + task.dueTimeString
        val time : Long = df.parse(timeStr).time

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time,pendingIntent)
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

        if(selectedCategory == "Все"){
            binding.listRecycleView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = TaskItemAdapter(liveList(), activity)
            }
        }else{
            binding.listRecycleView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = TaskItemAdapter(filteredList(selectedCategory), activity)
            }
        }
    }

    private fun liveList() : ArrayList<TaskItem>{
        val filteredTaskList = arrayListOf<TaskItem>()

        taskList.forEach {
            if(it.status != "done"){
                filteredTaskList.add(it)
            }
        }

        return filteredTaskList
    }
    private fun filteredList(selectedCategory: String) : ArrayList<TaskItem>{
        val filteredTaskList = arrayListOf<TaskItem>()

        taskList.forEach {
            if(it.category == selectedCategory && it.status != "done"){
                filteredTaskList.add(it)
            }
        }

        return filteredTaskList
    }

    override fun editTaskItem(taskItem: TaskItem) {
        NewTaskSheet(taskItem,taskList).show(childFragmentManager, "editTaskTag")
    }

    override fun completeTaskItem(taskItem: TaskItem) {
        if(taskItem.status == "live"){
            taskItem.status = "completed"
            taskItem.completedDateString = LocalDate.now().toString()
        }else if(taskItem.status == "completed"){
            taskItem.status = "live"
            taskItem.completedDateString = "null"
        }
        updateItem(taskItem)
        setRecycleView()
    }

    override fun deleteTaskItem(taskItem: TaskItem) {
        if(taskItem.status == "done" || taskItem.status == "live"){
            databaseRef.child(taskItem.id.toString()).removeValue()
        }else if (taskItem.status == "completed"){
            taskItem.status = "done"
            updateItem(taskItem)
        }
        if(taskItem.notificationId != 0){
            cancelScheduledNotification(taskItem.notificationId!!)
        }
    }

    private fun updateItem(taskItem: TaskItem) {
        val map = HashMap<String, Any>()
        map[taskItem.id.toString()] = taskItem
        databaseRef.updateChildren(map)
    }

    override fun setCategory(category: String) {
        selectedCategory = category
        setCategoriesView(categories)
        setRecycleView()
    }
}