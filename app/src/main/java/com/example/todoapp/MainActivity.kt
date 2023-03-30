package com.example.todoapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.ui.notes.NoteItem.NoteItemAdapter
import com.example.todoapp.ui.notes.NoteItem.NoteItemClickListener
import com.example.todoapp.ui.home.TaskItem.TaskItemAdapter
import com.example.todoapp.ui.home.TaskItem.TaskItemClickListener
import com.example.todoapp.ui.notes.NoteItem.NoteItem
import com.example.todoapp.ui.notes.NoteItem.NoteItemModelFactory
import com.example.todoapp.ui.notes.NoteItem.NoteViewModel
import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.example.todoapp.databinding.ActivityMainBinding
import com.example.todoapp.ui.home.TaskItem.TaskItemModelFactory
import com.example.todoapp.ui.home.TaskItem.TaskViewModel
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity(), TaskItemClickListener, NoteItemClickListener {

    private lateinit var binding: ActivityMainBinding


    private val taskViewModel: TaskViewModel by viewModels {
        TaskItemModelFactory((application as TodoApplication).repository)
    }
    private val noteViewModel: NoteViewModel by viewModels {
        NoteItemModelFactory((application as TodoApplication).notesRepository)
    }

    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.sideNavMenu.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_stat -> {
                    val int = Intent(this,StatisticActivity::class.java)
                    startActivity(int)
                }
                R.id.fav_tasks -> Toast.makeText(applicationContext, "Tasks", Toast.LENGTH_SHORT).show()
                R.id.fav_notes -> Toast.makeText(applicationContext, "Notes", Toast.LENGTH_SHORT).show()
            }
            true
        }

        setRecycleView()
        setNotesRecycleView()
        requestNotificationPermission()
        createNotificationChannel()
        //scheduleNotification()
    }

    private fun scheduleNotification() {
        val intent = Intent(this, NotificationReceiver::class.java)
        val title = "title"
        val message = "message"
        intent.putExtra(titleExtra,title)
        intent.putExtra(messageExtra,message)

        val pendingIntent = PendingIntent.getBroadcast(this, 0,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val df = SimpleDateFormat("yyyy.MM.dd-HH:mm")
        val time : Long = df.parse("2023.03.30-13:10").time

        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + 30000,pendingIntent)
    }

    private fun createNotificationChannel() {
        val name = "Name"
        val desc = "Desc"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId,name,importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun requestNotificationPermission() {
        notificationPermissionLauncher.launch(
            android.Manifest.permission.POST_NOTIFICATIONS
        )
        notificationPermissionLauncher.launch(
            android.Manifest.permission.SCHEDULE_EXACT_ALARM
        )
    }

    private fun setRecycleView() {
        val activity = this
        taskViewModel.taskItems.observe(this){
            binding.listRecycleView.apply {
                layoutManager = LinearLayoutManager(applicationContext)
                adapter = TaskItemAdapter(it, activity)
            }
        }
    }

    private fun setNotesRecycleView() {
        val activity = this

        noteViewModel.noteItems.observe(this){
            binding.notesRecycleView.apply {
                layoutManager = LinearLayoutManager(applicationContext)
                adapter = NoteItemAdapter(it, activity)
            }
        }
    }

    override fun editTaskItem(taskItem: TaskItem) {
        NewTaskSheet(taskItem).show(supportFragmentManager, "editTaskTag")
    }

    override fun completeTaskItem(taskItem: TaskItem) {
        taskViewModel.setTaskCompleted(taskItem)
    }

    override fun deleteTaskItem(taskItem: TaskItem) {
        taskViewModel.deleteTaskItem(taskItem)
    }

    override fun editNoteItem(noteItem: NoteItem) {
        NewNoteSheet(noteItem).show(supportFragmentManager, "editTaskTag")
    }

    override fun deleteNoteItem(noteItem: NoteItem) {
        TODO("Not yet implemented")
    }
}