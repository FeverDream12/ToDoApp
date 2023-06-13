package com.example.todoapp.mainMenu.notes.AudioNoteItem;

interface AudioNoteItemClickListener {

    fun deleteAudioNoteItem(audioNoteItem: AudioNoteItem)

    fun editAudioNoteItem(audioNoteItem: AudioNoteItem)

    fun setAudioNoteFavorite(audioNoteItem: AudioNoteItem)

}
