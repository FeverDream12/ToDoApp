package com.example.todoapp.utilities

import android.app.*
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.todoapp.R
import com.example.todoapp.channelId
import com.example.todoapp.databinding.ActivityMainBinding
import com.example.todoapp.repeatedTasks.RepeatedTasksActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
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
                    val int = Intent(this, StatisticActivity::class.java)
                    startActivity(int)
                }
                R.id.nav_fav -> {
                val int = Intent(this, FavouritesActivity::class.java)
                    startActivity(int)
                }
                R.id.repTasks -> {
                    val int = Intent(this, RepeatedTasksActivity::class.java)
                    startActivity(int)
                }
                R.id.nav_logout -> {
                    auth.signOut()
                    val int = Intent(this, AuthActivity::class.java)
                    startActivity(int)
                }
            }
            true
        }

        requestNotificationPermission()
        createNotificationChannel()
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
        notificationPermissionLauncher.launch(
            android.Manifest.permission.RECORD_AUDIO
        )
        notificationPermissionLauncher.launch(
            android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
        )
        notificationPermissionLauncher.launch(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        notificationPermissionLauncher.launch(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )


    }
}