package com.example.todoapp.itemSheets

import android.content.ContextWrapper
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import com.example.todoapp.databinding.FragmentNewAudioNoteSheetBinding
import com.example.todoapp.mainMenu.notes.AudioNoteItem.AudioNoteItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class NewAudioNoteSheet(var audioNoteItem: AudioNoteItem?) : BottomSheetDialogFragment(){

    private lateinit var binding: FragmentNewAudioNoteSheetBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var mediaRecorder: MediaRecorder


    private lateinit var noteUrl: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var recStatus = "null"

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("AudioNoteItems").child(auth.currentUser?.uid.toString())

        if(audioNoteItem != null){
            binding.recBtn.visibility = ImageButton.GONE
            binding.taskTitle.text = "Изменить"
            val editable = Editable.Factory.getInstance()
            binding.editTittleText.text = editable.newEditable(audioNoteItem!!.title)
        }

        binding.recBtn.setOnClickListener{
            if(recStatus != "Recording"){

                recStatus = "Recording"
                binding.recBtn.visibility = ImageView.GONE
                binding.stopBtn.visibility = ImageView.VISIBLE
                binding.saveButton.text = "Идёт запись..."

                mediaRecorder = MediaRecorder()
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
                mediaRecorder.setAudioEncodingBitRate(24);
                mediaRecorder.setAudioSamplingRate(44100);
                mediaRecorder.setOutputFile(getPath())
                mediaRecorder.prepare()
                mediaRecorder.start()
            }
        }

        binding.stopBtn.setOnClickListener {
            if(recStatus == "Recording"){
                recStatus = "Done"
                binding.recBtn.visibility = ImageView.VISIBLE
                binding.stopBtn.visibility = ImageView.GONE
                binding.saveButton.text = "Сохранить"

                mediaRecorder.stop()
                mediaRecorder.release()
            }
        }

        binding.saveButton.setOnClickListener{
            if(recStatus == "Done" || audioNoteItem != null){
                saveAction()
            }
        }

    }

    private fun updateItem(audioNoteItem: AudioNoteItem) {
        val map = HashMap<String, Any>()
        map[audioNoteItem.id.toString()] = audioNoteItem
        databaseRef.updateChildren(map)
    }

    private fun saveAction() {

        if(audioNoteItem == null){
            noteUrl = "error"
            storageRef = FirebaseStorage.getInstance().reference.child("AudioNotes").child(auth.currentUser?.uid.toString())
            val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm")

            val audioRef = storageRef.child(Random.nextInt(0, 100000).toString() + ".mp3")

            binding.saveButton.text = "Сохранение..."

            audioRef.putFile(Uri.fromFile(File(getPath()))).addOnSuccessListener { taskSnapshot ->
                noteUrl = audioRef.toString()

                val title = binding.editTittleText.text.toString()
                val date = formatter.format(Date())

                val newAudioNote = AudioNoteItem(title,date,"false",noteUrl)

                val noteId = databaseRef.push().key!!
                databaseRef.child(noteId).setValue(newAudioNote)

                dismiss()
            }
        }else{
            audioNoteItem!!.title = binding.editTittleText.text.toString()
            updateItem(audioNoteItem!!)
            dismiss()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        binding = FragmentNewAudioNoteSheetBinding.inflate( inflater,container, false)
        return binding.root
    }

    fun getPath() : String{
        val dir = ContextWrapper(requireContext()).getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(dir,"rec.mp3")

        return  file.absolutePath
    }

}