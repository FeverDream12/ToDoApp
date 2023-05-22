package com.example.todoapp.utilities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.example.todoapp.R
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.FileInputStream

class AuthActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        Firebase.database.setPersistenceEnabled(true)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        val path = applicationContext.filesDir
        val letDirectory = File(path, "LET")
        letDirectory.mkdirs()

        val settingsFile = File(letDirectory, "settings.txt")

        if(settingsFile.exists()){
            val themeMode = FileInputStream(settingsFile).bufferedReader().use { it.readText() }

            if(themeMode == "night"){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else if (themeMode == "day"){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }
}