package com.example.todoapp.mainMenu.notes.AudioNoteItem

import android.content.Context
import android.content.ContextWrapper
import android.media.MediaPlayer
import android.os.Environment
import android.os.Handler
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.AudionoteItemCellBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class AudioNoteItemViewHolder(
    private val context: Context,
    private val binding: AudionoteItemCellBinding,
    private val clickListener: AudioNoteItemClickListener
) : RecyclerView.ViewHolder(binding.root){

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var mediaPlayer: MediaPlayer

    fun bindAudioNoteItem(audioNoteItem: AudioNoteItem){
        binding.audiNoteTitle.text = audioNoteItem.title
        binding.audioNoteDate.text = audioNoteItem.date

        if(audioNoteItem.isFavourite == "true"){
            binding.favourite.visibility = ImageButton.VISIBLE
        }

        binding.audioNotesCard.setOnClickListener{
            clickListener.editAudioNoteItem(audioNoteItem)
        }

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("AudioNoteItems").child(auth.currentUser?.uid.toString())
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(audioNoteItem.audioUrl.toString())

        val dir = ContextWrapper(context).getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(dir,audioNoteItem.id.toString() +".mp3")

        if(file.exists()){
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(file.absolutePath)
            mediaPlayer.prepare()
            getDuration(mediaPlayer)
        }else{
            binding.playButton.visibility = ImageView.GONE
            binding.loadingButton.visibility = ImageView.VISIBLE

            storageRef.getFile(file).addOnSuccessListener {
                mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(file.absolutePath)
                mediaPlayer.prepare()
                binding.playButton.visibility = ImageView.VISIBLE
                binding.loadingButton.visibility = ImageView.GONE

                getDuration(mediaPlayer)
            }
        }

        binding.audioSeekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {
                mediaPlayer.seekTo(p0!!.progress)
            }
        })

        binding.playButton.setOnClickListener{
            binding.pauseButton.visibility = ImageView.VISIBLE
            binding.playButton.visibility = ImageView.GONE
            mediaPlayer.start()

            val handler = Handler()
            handler.postDelayed(object: Runnable{
                override fun run() {
                    try{
                        binding.audioSeekBar.progress = mediaPlayer.currentPosition
                        handler.postDelayed(this,1000/60)
                    }catch(e: java.lang.Exception){
                        binding.audioSeekBar.progress = 0
                    }
                }
            },0)
        }

        binding.pauseButton.setOnClickListener{
            binding.pauseButton.visibility = ImageView.GONE
            binding.playButton.visibility = ImageView.VISIBLE
            mediaPlayer.pause()
        }
    }

    private fun getDuration(mediaPlayer: MediaPlayer) {

        var durationHour = (((mediaPlayer.duration / 1000) / 60) / 60).toInt().toString()
        var durationMin = ((mediaPlayer.duration / 1000) / 60).toInt().toString()
        var durationSec = ((mediaPlayer.duration / 1000) % 60).toInt().toString()

        if(durationMin.length == 1){
            durationMin = "0" + durationMin
        }
        if(durationSec.length == 1){
            durationSec = "0" + durationSec
        }
        if(durationHour.length == 1){
            durationHour = "0" + durationHour
        }

        var durationStr = ""

        if(durationHour != "00"){
            durationStr = durationHour + ":" + durationMin + ":" + durationSec
        }else{
            durationStr = durationMin + ":" + durationSec
        }

        binding.audioSeekBar.max = mediaPlayer.duration
        binding.durationTimeText.text = durationStr
    }
}