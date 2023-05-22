package com.example.todoapp

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.example.todoapp.utilities.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate


class HomescreenWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if(context != null){
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context,javaClass))
            val prefs = context.getSharedPreferences(context.packageName,Context.MODE_PRIVATE)
            var auth = FirebaseAuth.getInstance()
            var databaseRef = FirebaseDatabase.getInstance().reference.child("TaskItems").child(auth.currentUser?.uid.toString())
            var taskList: ArrayList<TaskItem> = arrayListOf()

            databaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    taskList.clear()
                    if(snapshot.exists()){
                        snapshot.children.map{
                            val task : TaskItem? = it.getValue(TaskItem::class.java)
                            task!!.id = it.key
                            if(task.dueDate == LocalDate.now().toString() && task.status != "done")
                                taskList.add(task!!)
                        }
                    }
                    var taskListStr = ""

                    taskList.forEach{
                        if(it.status == "live"){
                            taskListStr += "☐ " + it.name + "\n"
                        }else{
                            taskListStr += "☑ " + it.name + "\n"
                        }
                    }

                    prefs.edit().putString("widget_tasks_list",taskListStr ).apply()

                    ids.forEach {
                            id ->updateAppWidget(context,manager,id)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    //
                }
            })
        }
    }

    override fun onEnabled(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context,javaClass))
        val prefs = context.getSharedPreferences(context.packageName,Context.MODE_PRIVATE)
        var auth = FirebaseAuth.getInstance()
        var databaseRef = FirebaseDatabase.getInstance().reference.child("TaskItems").child(auth.currentUser?.uid.toString())
        var taskList: ArrayList<TaskItem> = arrayListOf()

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                taskList.clear()
                if(snapshot.exists()){
                    snapshot.children.map{
                        val task : TaskItem? = it.getValue(TaskItem::class.java)
                        task!!.id = it.key
                        if(task.dueDate == LocalDate.now().toString() && task.status != "done")
                            taskList.add(task!!)
                    }
                }
                var taskListStr = ""

                taskList.forEach{
                    if(it.status == "live"){
                        taskListStr += "☐ " + it.name + "\n"
                    }else{
                        taskListStr += "☑ " + it.name + "\n"
                    }
                }

                prefs.edit().putString("widget_tasks_list",taskListStr).apply()

                ids.forEach {
                        id ->updateAppWidget(context,manager,id)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }
        })

//        ids.forEach {
//            id ->updateAppWidget(context,manager,id)
//        }
    }

    override fun onDisabled(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context,javaClass))
        val prefs = context.getSharedPreferences(context.packageName,Context.MODE_PRIVATE)
        var auth = FirebaseAuth.getInstance()
        var databaseRef = FirebaseDatabase.getInstance().reference.child("TaskItems").child(auth.currentUser?.uid.toString())
        var taskList: ArrayList<TaskItem> = arrayListOf()

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                taskList.clear()
                if(snapshot.exists()){
                    snapshot.children.map{
                        val task : TaskItem? = it.getValue(TaskItem::class.java)
                        task!!.id = it.key
                        if(task.dueDate == LocalDate.now().toString() && task.status != "done")
                            taskList.add(task!!)
                    }
                }
                var taskListStr = ""

                taskList.forEach{
                    if(it.status == "live"){
                        taskListStr += "☐ " + it.name + "\n"
                    }else{
                        taskListStr += "☑ " + it.name + "\n"
                    }
                }

                prefs.edit().putString("widget_tasks_list",taskListStr).apply()

                ids.forEach {
                        id ->updateAppWidget(context,manager,id)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }
        })
    }


    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.homescreen_widget)
        val prefs = context.getSharedPreferences(context.packageName,Context.MODE_PRIVATE)

        views.setOnClickPendingIntent(R.id.homescreen_widget,PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE))

        views.setTextViewText(R.id.todayTasksList, prefs.getString("widget_tasks_list", "0"))

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
