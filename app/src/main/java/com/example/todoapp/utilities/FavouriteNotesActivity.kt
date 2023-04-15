package com.example.todoapp.utilities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todoapp.itemSheets.NewNoteSheet
import com.example.todoapp.databinding.ActivityFavouriteNotesBinding
import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.example.todoapp.ui.notes.NoteItem.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FavouriteNotesActivity : AppCompatActivity(), NoteItemClickListener {

    private lateinit var binding: ActivityFavouriteNotesBinding

    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var notesList: ArrayList<NoteItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavouriteNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("NoteItems").child(auth.currentUser?.uid.toString())
        notesList = arrayListOf<NoteItem>()

        setNotesRecycleView()

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notesList.clear()
                if(snapshot.exists()){
                    snapshot.children.map{
                        val note : NoteItem? = it.getValue(NoteItem::class.java)
                        note!!.id = it.key
                        if(note.isFavourite == "true"){
                            notesList.add(note!!)
                        }
                    }
                }
                setNotesRecycleView()
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }
        })

        binding.backFavNotesButton.setOnClickListener{
            finish()
        }
    }

    private fun setNotesRecycleView() {
        val activity = this

        binding.favNotesRecycleView.apply {
                binding.favNotesRecycleView.setHasFixedSize(true)
                binding.favNotesRecycleView.layoutManager = StaggeredGridLayoutManager(1, LinearLayout.VERTICAL)
                layoutManager = LinearLayoutManager(context)
                layoutManager = StaggeredGridLayoutManager(1, LinearLayout.VERTICAL)
                adapter= NoteItemAdapter(notesList, activity)
            }
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