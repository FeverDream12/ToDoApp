package com.example.todoapp.utilities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.todoapp.itemSheets.NewNoteSheet
import com.example.todoapp.databinding.ActivityFavouriteNotesBinding
import com.example.todoapp.ui.notes.NoteItem.*

class FavouriteNotesActivity : AppCompatActivity(), NoteItemClickListener {

    private lateinit var binding: ActivityFavouriteNotesBinding

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

//        noteViewModel.favouriteNoteItems().observe(this){
//            binding.favNotesRecycleView.apply {
//                binding.favNotesRecycleView.setHasFixedSize(true)
//                binding.favNotesRecycleView.layoutManager = StaggeredGridLayoutManager(1, LinearLayout.VERTICAL)
//                layoutManager = LinearLayoutManager(context)
//                layoutManager = StaggeredGridLayoutManager(1, LinearLayout.VERTICAL)
//                adapter= NoteItemAdapter(it, activity)
//            }
//        }
    }

    override fun editNoteItem(noteItem: NoteItem) {
        NewNoteSheet(noteItem).show(supportFragmentManager, "editTaskTag")
    }

    override fun deleteNoteItem(noteItem: NoteItem) {
        //noteViewModel.deleteNoteItem(noteItem)
    }

    override fun setNoteFavorite(noteItem: NoteItem) {
        if(noteItem.isFavourite == "true"){
            noteItem.isFavourite = "false"
        }else{
            noteItem.isFavourite = "true"
        }
    }
}