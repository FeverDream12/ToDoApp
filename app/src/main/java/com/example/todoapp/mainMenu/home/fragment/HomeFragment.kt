package com.example.todoapp.mainMenu.home.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.*
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.itemSheets.NewTaskSheet
import com.example.todoapp.mainMenu.home.Category.CategoryAdapter
import com.example.todoapp.mainMenu.home.Category.CategoryItemClickListener
import com.example.todoapp.mainMenu.home.TaskItem.TaskItem
import com.example.todoapp.mainMenu.home.TaskItem.TaskItemAdapter
import com.example.todoapp.mainMenu.home.TaskItem.TaskItemClickListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.text.SimpleDateFormat
import java.time.LocalDate


class HomeFragment : Fragment(), TaskItemClickListener, CategoryItemClickListener {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var anim: Animation
    private lateinit var animRight: Animation
    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var taskList: ArrayList<TaskItem>

    private var selectedCategory = "Все"
    private var searchQuery = ""
    private var sortKey = "id"
    private var sortOrder = "asc"
    private var firstLoad = true
    private var updated = false
    private var selectedCategoryId = 1
    private lateinit var categories : ArrayList<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("TaskItems").child(auth.currentUser?.uid.toString())
        taskList = arrayListOf()

        binding.newTaskButton.setOnClickListener{
            NewTaskSheet(null,taskList, null).show(childFragmentManager, "newTaskTag")
        }

        anim = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation).animation
        animRight = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation_right).animation
        binding.listRecycleView.startAnimation(anim)

        deleteSwipe()
        popupMenu()

        setCategoriesList()
        setCategory(selectedCategory)

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
                    "priority" ->{
                        taskList.sortBy { it.priority}
                    }
                }
                if(sortOrder == "desc"){
                    taskList.reverse()
                }
                updated = true
                setCategoriesList()
                setCategory(selectedCategory)
                setRecycleView(searchQuery)
                if(firstLoad){
                    firstLoad = false
                    binding.listRecycleView.startAnimation(anim)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }
        })


        binding.tasksSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if(newText != null){
                    searchQuery = newText
                    if(searchQuery != ""){
                        setRecycleView(searchQuery)
                    }else{
                        setRecycleView("")
                    }
                }
                return true
            }
        })

        return binding.root
    }

    private fun deleteSwipe(){
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
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
                    item = liveList(searchQuery)[position]
                }else{
                    item = filteredList(selectedCategory,searchQuery)[position]
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
                        setTaskFavorite(item)
                    }
                }
            }

            override fun onChildDraw(c: Canvas,recyclerView: RecyclerView,viewHolder: RecyclerView.ViewHolder,dX: Float,dY: Float,actionState: Int,isCurrentlyActive: Boolean) {

                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftActionIcon(R.drawable.delete_24)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(),R.color.red2))
                    .addSwipeLeftCornerRadius(1, 15F)
                    .addSwipeRightActionIcon(R.drawable.baseline_star_30)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(requireContext(),R.color.fav))
                    .addSwipeRightCornerRadius(1, 15F)
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
        popupMenu.setForceShowIcon(true)

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.searchTasks ->{
                    if(binding.searchView.visibility == LinearLayout.VISIBLE){
                        binding.searchView.visibility = LinearLayout.GONE
                        binding.tasksSearchView.setQuery("",false)
                        searchQuery = ""
                    }else{
                        binding.searchView.visibility = LinearLayout.VISIBLE
                    }
                    updated = true
                    setCategory("Все")
                    true
                }
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
                R.id.priority_sort_asc ->{
                    sortKey = "priority"
                    sortOrder = "asc"
                    refreshList()
                    true
                }
                R.id.priority_sort_desc ->{
                    sortKey = "priority"
                    sortOrder = "desc"
                    refreshList()
                    true
                }
                R.id.hideCompleteTasks ->{
                    liveList(searchQuery).forEach {
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

    private fun getPriority(priority: String) : String{
        var priorityStr = ""

        when(priority){
            "0" -> priorityStr = "Обычный"
            "1" -> priorityStr = "Средний"
            "2" -> priorityStr = "Высокий"
        }

        return priorityStr
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

    private fun setRecycleView(searchQuery: String) {
        val activity = this

        if(selectedCategory == "Все"){
            binding.listRecycleView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = TaskItemAdapter(liveList(searchQuery), activity)
            }
        }else{
            binding.listRecycleView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = TaskItemAdapter(filteredList(selectedCategory,searchQuery), activity)
            }
        }
    }
    private fun liveList(searchQuery: String) : ArrayList<TaskItem>{
        val liveList = arrayListOf<TaskItem>()
        val liveSearchList = arrayListOf<TaskItem>()

        taskList.forEach {
            if(it.status != "done"){
                liveList.add(it)
            }
        }

        if (searchQuery != ""){
            liveList.forEach{
                if (it.name!!.lowercase().contains(searchQuery.lowercase()) || it.desc!!.lowercase().contains(searchQuery.lowercase())){
                    liveSearchList.add(it)
                }
            }
            return liveSearchList
        }else{
            return liveList
        }
    }
    private fun filteredList(selectedCategory: String,searchQuery: String) : ArrayList<TaskItem>{
        val filteredTaskList = arrayListOf<TaskItem>()
        val filteredSearchList = arrayListOf<TaskItem>()

        taskList.forEach {
            if(it.category == selectedCategory && it.status != "done"){
                filteredTaskList.add(it)
            }
        }

        if (searchQuery != ""){
            filteredTaskList.forEach{
                if (it.name!!.lowercase().contains(searchQuery.lowercase()) || it.desc!!.lowercase().contains(searchQuery.lowercase())){
                    filteredSearchList.add(it)
                }
            }
            return filteredSearchList
        }else{
            return filteredTaskList
        }
    }

    override fun editTaskItem(taskItem: TaskItem) {
        NewTaskSheet(taskItem,taskList,null).show(childFragmentManager, "editTaskTag")
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
        setRecycleView(searchQuery)
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

    override fun setTaskFavorite(taskItem: TaskItem) {
        if(taskItem.isFavourite == "false"){
            taskItem.isFavourite = "true"
        }else{
            taskItem.isFavourite = "false"
        }
        updateItem(taskItem)
    }

    override fun rescheduleTaskItem(taskItem: TaskItem, time: String) {
        when(time){
            "day" ->{
                taskItem.dueDate = LocalDate.parse(taskItem.dueDate).plusDays(1).toString()
                updateItem(taskItem)
            }
            "week" ->{
                taskItem.dueDate = LocalDate.parse(taskItem.dueDate).plusWeeks(1).toString()
                updateItem(taskItem)
            }
            "month" ->{
                taskItem.dueDate = LocalDate.parse(taskItem.dueDate).plusMonths(1).toString()
                updateItem(taskItem)
            }
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
        setRecycleView(searchQuery)

        if(!updated){
            if(getCategoryId(category) > selectedCategoryId){
                binding.listRecycleView.startAnimation(animRight)
            }else{
                binding.listRecycleView.startAnimation(anim)
            }
        }
        updated = false
        selectedCategoryId = getCategoryId(category)
    }

    fun getCategoryId(category: String) : Int{
        var id = 0

        categories.forEach{
            id++
            if(it == category ){
                return id
            }
        }
        return id
    }
}