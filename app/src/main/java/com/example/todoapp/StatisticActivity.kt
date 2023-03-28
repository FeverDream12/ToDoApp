package com.example.todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.databinding.ActivityStatisticBinding
import com.example.todoapp.ui.home.TaskItem.*

class StatisticActivity : AppCompatActivity(){

    private lateinit var binding: ActivityStatisticBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStatisticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backStatButton.setOnClickListener{
            finish()
        }
    }

}