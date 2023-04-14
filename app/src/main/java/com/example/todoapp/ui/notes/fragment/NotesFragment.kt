package com.example.todoapp.ui.notes.fragment

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todoapp.ui.notes.NoteItem.NoteItemAdapter
import com.example.todoapp.ui.notes.NoteItem.NoteItemClickListener
import com.example.todoapp.ui.notes.NoteItem.NoteItem
import com.example.todoapp.databinding.FragmentNotesBinding
import com.example.todoapp.itemSheets.NewNoteSheet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class NotesFragment : Fragment(), NoteItemClickListener {

    private lateinit var binding: FragmentNotesBinding

    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var notesList: ArrayList<NoteItem>

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNotesBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("NoteItems").child(auth.currentUser?.uid.toString())
        notesList = arrayListOf<NoteItem>()


        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notesList.clear()
                if(snapshot.exists()){
                    snapshot.children.map{
                        val note : NoteItem? = it.getValue(NoteItem::class.java)
                        note!!.id = it.key
                        notesList.add(note!!)
                    }
                }
                setNotesRecycleView()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })



        binding.newNoteButton.setOnClickListener{
            NewNoteSheet(null).show(childFragmentManager, "newNoteTag")
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if(newText != null){
                    if(newText!= ""){
                        //setFilteredNotesRecycleView(newText)
                    }else{
                        //setNotesRecycleView()
                    }
                }
                return true
            }
        })
        setNotesRecycleView()
        return binding.root
    }

    private fun setNotesRecycleView() {
        val activity = this

        binding.NotesRecycleView.apply {
            binding.NotesRecycleView.setHasFixedSize(true)
            binding.NotesRecycleView.layoutManager = StaggeredGridLayoutManager(2,LinearLayout.VERTICAL)
            layoutManager = LinearLayoutManager(context)
            layoutManager = StaggeredGridLayoutManager(2,LinearLayout.VERTICAL)
            adapter= NoteItemAdapter(notesList, activity)
        }
    }

//    private fun setFilteredNotesRecycleView(string: String) {
//        val activity = this
//
//        noteViewModel.searchNoteItem(string).observe(viewLifecycleOwner){
//            binding.NotesRecycleView.apply {
//                binding.NotesRecycleView.setHasFixedSize(true)
//                binding.NotesRecycleView.layoutManager = StaggeredGridLayoutManager(2,LinearLayout.VERTICAL)
//                layoutManager = LinearLayoutManager(context)
//                adapter= NoteItemAdapter(it, activity)
//            }
//        }
//
//    }

    override fun editNoteItem(noteItem: NoteItem) {
        NewNoteSheet(noteItem).show(childFragmentManager, "editTaskTag")
    }

    override fun deleteNoteItem(noteItem: NoteItem) {
        databaseRef.child(noteItem.id.toString()).removeValue()
    }

    private fun updateItem(noteItem: NoteItem) {
        val map = HashMap<String, Any>()
        map[noteItem.id.toString()] = noteItem
        databaseRef.updateChildren(map)
    }

    override fun setNoteFavorite(noteItem: NoteItem) {
        if(noteItem.isFavourite == "true"){
            noteItem.isFavourite = "false"
        }else{
            noteItem.isFavourite = "true"
        }
        updateItem(noteItem)
    }
}