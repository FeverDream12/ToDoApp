package com.example.todoapp.mainMenu.home.Category

import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.databinding.CategoryViewItemBinding

class CategoryViewHolder(
    private val binding: CategoryViewItemBinding,
    private val clickListener: CategoryItemClickListener
): RecyclerView.ViewHolder(binding.root){

    var category: TextView = itemView.findViewById(R.id.categoryViewName)


    fun bindCategory(selectedCategory: String){

        if(binding.categoryViewName.text.toString() == selectedCategory){
            binding.backCard.visibility = CardView.INVISIBLE
        }else{
            binding.backCard.visibility = CardView.VISIBLE
        }

        binding.categoryCard.setOnClickListener(){
            clickListener.setCategory(binding.categoryViewName.text.toString())
        }
    }
}