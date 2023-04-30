package com.example.todoapp.ui.notes.AudioNoteItem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.AudionoteItemCellBinding
import com.example.todoapp.databinding.NoteItemCellBinding
import com.example.todoapp.ui.notes.NoteItem.NoteItemViewHolder


class AudioNoteItemAdapter(
    private val audioNoteItems: List<AudioNoteItem>,
    private val clickListener: AudioNoteItemClickListener
): RecyclerView.Adapter<AudioNoteItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioNoteItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = AudionoteItemCellBinding.inflate(from, parent, false)
        return AudioNoteItemViewHolder(parent.context, binding, clickListener)
    }

    override fun getItemCount(): Int {
        return audioNoteItems.size
    }

    override fun onBindViewHolder(holder: AudioNoteItemViewHolder, position: Int) {
        holder.bindAudioNoteItem(audioNoteItems[position])
    }
}