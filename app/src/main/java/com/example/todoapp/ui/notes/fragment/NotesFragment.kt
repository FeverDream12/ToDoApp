package com.example.todoapp.ui.notes.fragment

import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todoapp.R
import com.example.todoapp.ui.notes.NoteItem.NoteItemAdapter
import com.example.todoapp.ui.notes.NoteItem.NoteItemClickListener
import com.example.todoapp.ui.notes.NoteItem.NoteItem
import com.example.todoapp.databinding.FragmentNotesBinding
import com.example.todoapp.itemSheets.NewAudioNoteSheet
import com.example.todoapp.itemSheets.NewNoteSheet
import com.example.todoapp.ui.home.TaskItem.TaskItem
import com.example.todoapp.ui.notes.AudioNoteItem.AudioNoteItem
import com.example.todoapp.ui.notes.AudioNoteItem.AudioNoteItemAdapter
import com.example.todoapp.ui.notes.AudioNoteItem.AudioNoteItemClickListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class NotesFragment : Fragment(), NoteItemClickListener, AudioNoteItemClickListener {

    private lateinit var binding: FragmentNotesBinding

    private lateinit var notesDatabaseRef: DatabaseReference
    private lateinit var audioNotesDatabaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var notesList: ArrayList<NoteItem>
    private lateinit var audioNotesList: ArrayList<AudioNoteItem>
    private var viewMod = "notes"

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNotesBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        notesDatabaseRef = FirebaseDatabase.getInstance().reference.child("NoteItems").child(auth.currentUser?.uid.toString())
        audioNotesDatabaseRef = FirebaseDatabase.getInstance().reference.child("AudioNoteItems").child(auth.currentUser?.uid.toString())

        notesList = arrayListOf()
        audioNotesList = arrayListOf()

        binding.audioNotesMod.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))

        binding.notesMod.setOnClickListener{
            if(viewMod != "notes"){
                viewMod = "notes"
                binding.notesMod.setTextColor(binding.audioNotesMod.currentTextColor)
                binding.audioNotesMod.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
                binding.notesModUnderline.visibility = CardView.VISIBLE
                binding.audioNotesModUnderline.visibility = CardView.GONE
                setNotesRecycleView()
            }
        }

        binding.audioNotesMod.setOnClickListener{
            if(viewMod != "audio"){
                viewMod = "audio"
                binding.audioNotesMod.setTextColor(binding.notesMod.currentTextColor)
                binding.notesMod.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
                binding.audioNotesModUnderline.visibility = CardView.VISIBLE
                binding.notesModUnderline.visibility = CardView.GONE
                setNotesRecycleView()
            }
        }

        notesDatabaseRef.addValueEventListener(object : ValueEventListener {
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

        audioNotesDatabaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                audioNotesList.clear()
                if(snapshot.exists()){
                    snapshot.children.map{
                        val note : AudioNoteItem? = it.getValue(AudioNoteItem::class.java)
                        note!!.id = it.key
                        audioNotesList.add(note!!)
                    }
                }
                setNotesRecycleView()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })


        binding.newNoteButton.setOnClickListener{
            when(viewMod){
                "notes" -> NewNoteSheet(null).show(childFragmentManager, "newNoteTag")
                "audio" -> NewAudioNoteSheet(null).show(childFragmentManager, "newAudioNoteTag")
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if(newText != null){
                    if(newText != ""){
                        setFilteredNotesRecycleView(newText)
                    }else{
                        setNotesRecycleView()
                    }
                }
                return true
            }
        })
        setNotesRecycleView()
        deleteSwipe()
        return binding.root
    }


    private fun setNotesRecycleView() {
        val activity = this

        if(viewMod == "notes"){
            binding.NotesRecycleView.apply {
                binding.NotesRecycleView.setHasFixedSize(true)
                binding.NotesRecycleView.layoutManager = StaggeredGridLayoutManager(2,LinearLayout.VERTICAL)
                layoutManager = LinearLayoutManager(context)
                layoutManager = StaggeredGridLayoutManager(2,LinearLayout.VERTICAL)
                adapter= NoteItemAdapter(notesList, activity)
            }
        }else if(viewMod == "audio"){
            binding.NotesRecycleView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter= AudioNoteItemAdapter(audioNotesList, activity)
            }
        }
    }

    private fun setFilteredNotesRecycleView(string: String) {
        val activity = this

        binding.NotesRecycleView.apply {
            binding.NotesRecycleView.setHasFixedSize(true)
            binding.NotesRecycleView.layoutManager = StaggeredGridLayoutManager(2,LinearLayout.VERTICAL)
            layoutManager = LinearLayoutManager(context)
            layoutManager = StaggeredGridLayoutManager(2,LinearLayout.VERTICAL)
            adapter= NoteItemAdapter(filteredNotesList(string), activity)
        }

    }

    private fun filteredNotesList(string: String): ArrayList<NoteItem> {
        val filteredNotesList = arrayListOf<NoteItem>()

        notesList.forEach {
            if(it.note!!.lowercase().contains(string.lowercase()) || it.title!!.lowercase().contains(string.lowercase())){
                filteredNotesList.add(it)
            }
        }

        return filteredNotesList
    }

    override fun editNoteItem(noteItem: NoteItem) {
        NewNoteSheet(noteItem).show(childFragmentManager, "editTaskTag")
    }

    override fun deleteNoteItem(noteItem: NoteItem) {
        notesDatabaseRef.child(noteItem.id.toString()).removeValue()
    }

    private fun updateItem(noteItem: NoteItem) {
        val map = HashMap<String, Any>()
        map[noteItem.id.toString()] = noteItem
        notesDatabaseRef.updateChildren(map)
    }

    private fun updateNoteItem(audioNoteItem: AudioNoteItem) {
        val map = HashMap<String, Any>()
        map[audioNoteItem.id.toString()] = audioNoteItem
        audioNotesDatabaseRef.updateChildren(map)
    }

    override fun setNoteFavorite(noteItem: NoteItem) {
        if(noteItem.isFavourite == "true"){
            noteItem.isFavourite = "false"
        }else{
            noteItem.isFavourite = "true"
        }
        updateItem(noteItem)
    }

    override fun deleteAudioNoteItem(audioNoteItem: AudioNoteItem) {
       audioNotesDatabaseRef.child(audioNoteItem.id.toString()).removeValue()
    }

    override fun setAudioNoteFavorite(audioNoteItem: AudioNoteItem) {
        if(audioNoteItem.isFavourite == "true"){
            audioNoteItem.isFavourite = "false"
        }else{
            audioNoteItem.isFavourite = "true"
        }
        updateNoteItem(audioNoteItem)
    }



    private fun deleteSwipe(){
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                var noteItem : NoteItem? = null
                var audioNoteItem : AudioNoteItem? = null

                if(viewMod == "notes"){
                    noteItem = notesList[position]
                }else if(viewMod == "audio"){
                    audioNoteItem = audioNotesList[position]
                }

                when (direction){
                    ItemTouchHelper.LEFT ->{

                        if(viewMod == "notes"){
                            deleteNoteItem(noteItem!!)

                            Snackbar.make(
                                binding.notesFrag,
                                "Заметка \"" + noteItem.title + "\" удалена",
                                Snackbar.LENGTH_SHORT
                            ).apply {
                                setAction("Отмена"){

                                    val noteId = notesDatabaseRef.push().key!!
                                    notesDatabaseRef.child(noteId).setValue(noteItem)
                                }
                                show()
                            }

                        }else if(viewMod == "audio"){

                            deleteAudioNoteItem(audioNoteItem!!)

                            Snackbar.make(
                                binding.notesFrag,
                                "Аудиозаметка \"" + audioNoteItem.title + "\" удалена",
                                Snackbar.LENGTH_SHORT
                            ).apply {
                                setAction("Отмена"){

                                    val noteId = audioNotesDatabaseRef.push().key!!
                                    audioNotesDatabaseRef.child(noteId).setValue(audioNoteItem)
                                }
                                show()
                            }
                        }


                    }
                    ItemTouchHelper.RIGHT ->{
                        if(viewMod == "notes"){
                            setNoteFavorite(noteItem!!)
                        }else if(viewMod == "audio"){
                            setAudioNoteFavorite(audioNoteItem!!)
                        }
                    }
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftActionIcon(R.drawable.delete_24)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(),R.color.red2))
                    .addSwipeLeftCornerRadius(1, 15F)
                    .addSwipeRightActionIcon(R.drawable.baseline_star_30)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(requireContext(),R.color.fav))
                    .addSwipeRightCornerRadius(1, 15F)
                    .create()
                    .decorate()

                super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)

            }
        }).attachToRecyclerView(binding.NotesRecycleView)
    }
}