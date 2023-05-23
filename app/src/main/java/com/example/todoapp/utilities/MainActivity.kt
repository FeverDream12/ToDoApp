package com.example.todoapp.utilities

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.todoapp.R
import com.example.todoapp.channelId
import com.example.todoapp.databinding.ActivityMainBinding
import com.example.todoapp.repeatedTasks.RepeatedTasksActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.FileInputStream

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        //Firebase.database.setPersistenceEnabled(true)

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        val path = applicationContext.filesDir
        val letDirectory = File(path, "LET")
        letDirectory.mkdirs()

        val settingsFile = File(letDirectory, "settings.txt")
        val exitFile = File(letDirectory, "exit.txt")

        exitFile.delete()

        if(settingsFile.exists()){
            val themeMode = FileInputStream(settingsFile).bufferedReader().use { it.readText() }

            if(themeMode == "night"){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else if (themeMode == "day"){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

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
                    exitFile.delete()
                    exitFile.appendText("exit")
                    finish()
                }
            }
            true
        }

        binding.modeSwitch.setOnClickListener{
            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                settingsFile.delete()
                settingsFile.appendText("day")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                settingsFile.delete()
                settingsFile.appendText("night")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            binding.modeText.text = "Тёмная"
            binding.modeImg.rotation = 180f
            binding.modeImg.setImageResource(R.drawable.mode_night_24)
            binding.modeImg.setColorFilter(ContextCompat.getColor(applicationContext,R.color.mode))
        }else if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
            binding.modeText.text = "Светлая"
            binding.modeImg.setImageResource(R.drawable.mode_sun_24)
            binding.modeImg.setColorFilter(ContextCompat.getColor(applicationContext,R.color.mode))
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
    }
}