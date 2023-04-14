package com.example.todoapp.ui.home.TaskItem

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.TaskItemCellBinding

class TaskItemAdapter(
    private val taskItems: List<TaskItem>,
    private val clickListener: TaskItemClickListener
):RecyclerView.Adapter<TaskItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = TaskItemCellBinding.inflate(from, parent, false)
        return TaskItemViewHolder(parent.context, binding, clickListener)
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<TaskItem>(){
        override fun areItemsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
            return true
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
            return  oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffCallBack)

    override fun getItemCount(): Int = taskItems.size

    override fun onBindViewHolder(holder: TaskItemViewHolder, position: Int) {
//        val item = differ.currentList[position]
        holder.bindTaskItem(taskItems[position])
    }
}