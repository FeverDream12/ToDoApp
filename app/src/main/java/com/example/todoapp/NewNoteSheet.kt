package com.example.todoapp

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.ui.notes.NoteItem.NoteItem
import com.example.todoapp.ui.notes.NoteItem.NoteViewModel
import com.example.todoapp.databinding.FragmentNewNoteSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class NewNoteSheet(var noteItem: NoteItem?) : BottomSheetDialogFragment(){

    private lateinit var binding: FragmentNewNoteSheetBinding
    private lateinit var noteViewModel: NoteViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        if(noteItem != null){
            binding.sheetTitle.text = "Изменить"
            val editable = Editable.Factory.getInstance()
            binding.editTittleText.text = editable.newEditable(noteItem!!.title)
            binding.editNoteText.text = editable.newEditable(noteItem!!.note)

        }else{
            binding.sheetTitle.text = "Новая заметка"
        }

        noteViewModel = ViewModelProvider(activity)[NoteViewModel::class.java]
        binding.doneButton.setOnClickListener{
            saveAction()
        }
        binding.backButton.setOnClickListener{
            dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewNoteSheetBinding.inflate( inflater,container, false)
        return binding.root
    }

    private fun saveAction() {
        val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm")
        val title = binding.editTittleText.text.toString()
        val note = binding.editNoteText.text.toString()
        val date = formatter.format(Date())

        if(title.isNotEmpty() || note.isNotEmpty()){
            if(noteItem == null)
            {
                val newNote = NoteItem(title,note,date)
                noteViewModel.addNoteItem(newNote)
            }else
            {
                noteItem!!.title = title
                noteItem!!.note = note
                noteItem!!.date = date
                noteViewModel.updateNoteItem(noteItem!!)
            }

            binding.editTittleText.setText("")
            binding.editNoteText.setText("")
            dismiss()
        }
    }
}