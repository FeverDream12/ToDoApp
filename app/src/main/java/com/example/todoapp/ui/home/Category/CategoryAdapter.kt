package com.example.todoapp.ui.home.Category


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.CategoryViewItemBinding
import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.example.todoapp.ui.home.TaskItem.TaskItemViewHolder
import com.example.todoapp.ui.home.TaskItem.TaskViewModel

class CategoryAdapter(
    private val categories : ArrayList<String>,
    private val clickListener: CategoryItemClickListener,
    private val taskViewModel: TaskViewModel,
    private val selectedCategory: String) : RecyclerView.Adapter<CategoryViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): CategoryViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CategoryViewItemBinding.inflate(from, parent, false)
        return CategoryViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int
    ) {
        holder.category.text = categories[position]
        holder.bindCategory(selectedCategory)
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}