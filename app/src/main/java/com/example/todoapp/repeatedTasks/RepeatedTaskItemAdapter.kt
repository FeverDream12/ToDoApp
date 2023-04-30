package com.example.todoapp.repeatedTasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.RepeatedTaskItemCellBinding


class RepeatedTaskItemAdapter(
    private val repeatedTaskItems: List<RepeatedTaskItem>,
    private val repeatedClickListener: RepeatedTaskItemClickListener,
    private val currentDay : String
): RecyclerView.Adapter<RepeatedTaskItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepeatedTaskItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = RepeatedTaskItemCellBinding.inflate(from, parent, false)
        return RepeatedTaskItemViewHolder(parent.context, binding, repeatedClickListener,currentDay)
    }

    override fun onBindViewHolder(holder: RepeatedTaskItemViewHolder, position: Int) {
        holder.bindRepeatedTaskItem(repeatedTaskItems[position])
    }

    override fun getItemCount(): Int = repeatedTaskItems.size
}