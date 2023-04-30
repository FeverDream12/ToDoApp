package com.example.todoapp.ui.notes.NoteItem

import android.content.Context
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.databinding.NoteItemCellBinding

class NoteItemViewHolder(
    private val context: Context,
    private val binding: NoteItemCellBinding,
    private val clickListener: NoteItemClickListener
) : RecyclerView.ViewHolder(binding.root){

    fun bindNoteItem(noteItem: NoteItem){
        binding.noteTitle.text = noteItem.title
        binding.noteText.text = noteItem.note
        binding.noteDate.text = noteItem.date

        popupMenu(noteItem)

        binding.notesCard.setOnClickListener{
            clickListener.editNoteItem(noteItem)
        }

        if(noteItem.isFavourite == "true"){
            binding.favourite.visibility = ImageButton.VISIBLE
        }
    }

    private fun popupMenu(noteItem: NoteItem) {
        val popupMenu = PopupMenu(context,binding.notesCard)
        popupMenu.inflate(R.menu.pop_up_menu)

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.delete_note ->{
                    clickListener.deleteNoteItem(noteItem)
                    true
                }
                else -> {
                    true
                }
            }
        }

        binding.notesCard.setOnLongClickListener{
            try {
                val popup = PopupMenu::class.java.getDeclaredField("mpop")
                popup.isAccessible = true

                val menu = popup.get(popupMenu)
                menu.javaClass
                    .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                    .invoke(menu, true)
            } catch (e: Exception){
                e.printStackTrace()
            }finally {
                popupMenu.show()
            }
            true
        }
    }

}