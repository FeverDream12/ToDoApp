package com.example.todoapp.mainMenu.home.Category

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.databinding.CategoryViewItemBinding

class CategoryViewHolder(
    private val binding: CategoryViewItemBinding,
    private val clickListener: CategoryItemClickListener
): RecyclerView.ViewHolder(binding.root){

    var category: TextView = itemView.findViewById(R.id.categoryViewName)


    @SuppressLint("ResourceAsColor")
    fun bindCategory(selectedCategory: String){

        if(binding.categoryViewName.text.toString() == selectedCategory){
            binding.backCard.visibility = CardView.INVISIBLE
            binding.categoryViewName.visibility = TextView.INVISIBLE
            binding.categorySelectedViewName.text = binding.categoryViewName.text
            binding.categorySelectedViewName.visibility = TextView.VISIBLE

        }else{
            binding.backCard.visibility = CardView.VISIBLE
            binding.categoryViewName.visibility = TextView.VISIBLE
            binding.categorySelectedViewName.text = binding.categoryViewName.text
            binding.categorySelectedViewName.visibility = TextView.INVISIBLE
        }

        binding.categoryCard.setOnClickListener(){
            clickListener.setCategory(binding.categoryViewName.text.toString())
        }
    }
}