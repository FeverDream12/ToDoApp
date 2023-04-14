package com.example.todoapp.itemSheets

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.todoapp.ui.notes.NoteItem.NoteItem
import com.example.todoapp.databinding.FragmentNewNoteSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class NewNoteSheet(var noteItem: NoteItem?) : BottomSheetDialogFragment(){

    private lateinit var binding: FragmentNewNoteSheetBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("NoteItems").child(auth.currentUser?.uid.toString())

        if(noteItem != null){
            binding.sheetTitle.text = "Изменить"
            val editable = Editable.Factory.getInstance()
            binding.editTittleText.text = editable.newEditable(noteItem!!.title)
            binding.editNoteText.text = editable.newEditable(noteItem!!.note)

        }else{
            binding.sheetTitle.text = "Новая заметка"
        }

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

    private fun updateItem(noteItem: NoteItem) {
        val map = HashMap<String, Any>()
        map[noteItem.id.toString()] = noteItem
        databaseRef.updateChildren(map)
    }

    private fun saveAction() {
        val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm")
        val title = binding.editTittleText.text.toString()
        val note = binding.editNoteText.text.toString()
        val date = formatter.format(Date())

        if(title.isNotEmpty() || note.isNotEmpty()){
            if(noteItem == null)
            {
                val newNote = NoteItem(title,note,date,"false")

                val noteId = databaseRef.push().key!!
                databaseRef.child(noteId).setValue(newNote)
            }else
            {
                noteItem!!.title = title
                noteItem!!.note = note
                noteItem!!.date = date
                updateItem(noteItem!!)
            }

            binding.editTittleText.setText("")
            binding.editNoteText.setText("")
            dismiss()
        }
    }
}