package com.example.todoapp.utilities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.NotificationReceiver
import com.example.todoapp.R
import com.example.todoapp.databinding.ActivityFavouriteTasksBinding
import com.example.todoapp.itemSheets.NewTaskSheet
import com.example.todoapp.messageExtra
import com.example.todoapp.titleExtra
import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.example.todoapp.ui.home.TaskItem.TaskItemAdapter
import com.example.todoapp.ui.home.TaskItem.TaskItemClickListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.text.SimpleDateFormat
import java.time.LocalDate

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
            adapter = TaskItemAdapter(favouriteList(), activity)
        }
    }

    private fun favouriteList(): List<TaskItem> {
        val favouriteList = arrayListOf<TaskItem>()

        taskList.forEach {
            if(it.isFavourite == "true"){
                favouriteList.add(it)
            }
        }

        return favouriteList
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

                item = favouriteList()[position]

                when (direction){
                    ItemTouchHelper.LEFT ->{
                        deleteTaskItem(item)

                        Snackbar.make(
                            binding.favTasksActivity,
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

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftActionIcon(R.drawable.delete_24)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(applicationContext, R.color.red2))
                    .addSwipeLeftCornerRadius(1, 15F)
                    .addSwipeRightActionIcon(R.drawable.baseline_star_30)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(applicationContext, R.color.fav))
                    .addSwipeRightCornerRadius(1, 15F)
                    .create()
                    .decorate()

                super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)

            }
        }).attachToRecyclerView(binding.favTasksRecycleView)
    }

    override fun editTaskItem(taskItem: TaskItem) {
        NewTaskSheet(taskItem,taskList).show(this.supportFragmentManager, "editTaskTag")
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
        setRecycleView("")
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

    private fun updateItem(taskItem: TaskItem) {
        val map = HashMap<String, Any>()
        map[taskItem.id.toString()] = taskItem
        databaseRef.updateChildren(map)
    }

    private fun cancelScheduledNotification(id: Int) {
        val intent = Intent(this, NotificationReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(this, id,intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)
    }

    private fun scheduleNotification(task: TaskItem) {
        val intent = Intent(this, NotificationReceiver::class.java)
        val title = task.name
        val message = task.desc

        intent.putExtra(titleExtra,title)
        intent.putExtra(messageExtra,message)

        val pendingIntent = PendingIntent.getBroadcast(this,task.notificationId!!,intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val df = SimpleDateFormat("yyyy-MM-dd-HH:mm")
        val timeStr : String = task.dueDate + "-" + task.dueTimeString
        val time : Long = df.parse(timeStr).time

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time,pendingIntent)
    }
}