package com.example.todoapp.ui.notes.fragment

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todoapp.*
import com.example.todoapp.ui.notes.NoteItem.NoteItemAdapter
import com.example.todoapp.ui.notes.NoteItem.NoteItemClickListener
import com.example.todoapp.ui.notes.NoteItem.NoteItem
import com.example.todoapp.ui.notes.NoteItem.NoteItemModelFactory
import com.example.todoapp.ui.notes.NoteItem.NoteViewModel
import com.example.todoapp.databinding.FragmentNotesBinding

class NotesFragment : Fragment(), NoteItemClickListener {

    private lateinit var binding: FragmentNotesBinding
    private val noteViewModel: NoteViewModel by viewModels {
        val application = requireContext()
        NoteItemModelFactory((application.applicationContext as TodoApplication).notesRepository)
    }


    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNotesBinding.inflate(inflater, container, false)

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
                        setFilteredNotesRecycleView(newText)
                    }else{
                        setNotesRecycleView()
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

        noteViewModel.noteItems.observe(viewLifecycleOwner){
            binding.NotesRecycleView.apply {
                binding.NotesRecycleView.setHasFixedSize(true)
                binding.NotesRecycleView.layoutManager = StaggeredGridLayoutManager(2,LinearLayout.VERTICAL)
                layoutManager = LinearLayoutManager(context)
                layoutManager = StaggeredGridLayoutManager(2,LinearLayout.VERTICAL)
                adapter= NoteItemAdapter(it, activity)
            }
        }
    }

    private fun setFilteredNotesRecycleView(string: String) {
        val activity = this

        noteViewModel.searchNoteItem(string).observe(viewLifecycleOwner){
            binding.NotesRecycleView.apply {
                binding.NotesRecycleView.setHasFixedSize(true)
                binding.NotesRecycleView.layoutManager = StaggeredGridLayoutManager(2,LinearLayout.VERTICAL)
                layoutManager = LinearLayoutManager(context)
                adapter= NoteItemAdapter(it, activity)
            }
        }

    }

    override fun editNoteItem(noteItem: NoteItem) {
        NewNoteSheet(noteItem).show(childFragmentManager, "editTaskTag")
    }

    override fun deleteNoteItem(noteItem: NoteItem) {
        noteViewModel.deleteNoteItem(noteItem)
    }
}