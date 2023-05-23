package com.example.todoapp.ui.notes.AudioNoteItem;

interface AudioNoteItemClickListener {

    fun deleteAudioNoteItem(audioNoteItem: AudioNoteItem)

    fun editAudioNoteItem(audioNoteItem: AudioNoteItem)

    fun setAudioNoteFavorite(audioNoteItem: AudioNoteItem)

}
