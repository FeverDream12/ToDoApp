package com.example.todoapp.mainMenu.notes.NoteItem

import android.content.Context
import android.content.Intent
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
        popupMenu.setForceShowIcon(true)

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.share ->{
                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "text/plane"
                    share.putExtra(Intent.EXTRA_TEXT,"\"" + noteItem.title + "\"\n" + noteItem.note + "\n" + noteItem.date)
                    val chooser = Intent.createChooser(share,"Поделиться через...")

                    context.startActivity(chooser)
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