package com.example.todoapp.ui.notes.NoteItem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.NoteItemCellBinding

class NoteItemAdapter(
    private val noteItems: List<NoteItem>,
    private val clickListener: NoteItemClickListener
):RecyclerView.Adapter<NoteItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteItemViewHolder {

        val from = LayoutInflater.from(parent.context)
        val binding = NoteItemCellBinding.inflate(from, parent, false)
        return NoteItemViewHolder(parent.context, binding, clickListener)
    }

    override fun onBindViewHolder(holder: NoteItemViewHolder, position: Int) {
        holder.bindNoteItem(noteItems[position])
    }

    override fun getItemCount(): Int {
        return noteItems.size
    }

}