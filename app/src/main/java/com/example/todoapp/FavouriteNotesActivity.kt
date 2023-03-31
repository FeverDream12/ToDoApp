package com.example.todoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todoapp.databinding.ActivityFavouriteNotesBinding
import com.example.todoapp.databinding.ActivityStatisticBinding
import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.example.todoapp.ui.notes.NoteItem.*

class FavouriteNotesActivity : AppCompatActivity(), NoteItemClickListener {

    private lateinit var binding: ActivityFavouriteNotesBinding
    private val noteViewModel: NoteViewModel by viewModels {
        NoteItemModelFactory((application as TodoApplication).notesRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavouriteNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNotesRecycleView()

        binding.backFavNotesButton.setOnClickListener{
            finish()
        }
    }

    private fun setNotesRecycleView() {
        val activity = this

        noteViewModel.favouriteNoteItems().observe(this){
            binding.favNotesRecycleView.apply {
                binding.favNotesRecycleView.setHasFixedSize(true)
                binding.favNotesRecycleView.layoutManager = StaggeredGridLayoutManager(1, LinearLayout.VERTICAL)
                layoutManager = LinearLayoutManager(context)
                layoutManager = StaggeredGridLayoutManager(1, LinearLayout.VERTICAL)
                adapter= NoteItemAdapter(it, activity)
            }
        }
    }

    override fun editNoteItem(noteItem: NoteItem) {
        NewNoteSheet(noteItem).show(supportFragmentManager, "editTaskTag")
    }

    override fun deleteNoteItem(noteItem: NoteItem) {
        noteViewModel.deleteNoteItem(noteItem)
    }

    override fun setNoteFavorite(noteItem: NoteItem) {
        if(noteItem.isFavourite == "true"){
            noteItem.isFavourite = "false"
        }else{
            noteItem.isFavourite = "true"
        }
    }
}