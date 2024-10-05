package com.dicoding.geotaggingjbg.ui.helper

import androidx.recyclerview.widget.DiffUtil
import com.dicoding.geotaggingjbg.data.database.Entity

class DiffCallback(private val oldNoteList: List<Entity>, private val newNoteList: List<Entity>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldNoteList.size
    override fun getNewListSize(): Int = newNoteList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldNoteList[oldItemPosition].id == newNoteList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldNote = oldNoteList[oldItemPosition]
        val newNote = newNoteList[newItemPosition]
        return oldNote.image == newNote.image && oldNote.jenTan == newNote.jenTan && oldNote.tanggal == newNote.tanggal && oldNote.kegiatan == newNote.kegiatan && oldNote.lokasi == newNote.lokasi
                && oldNote.latitude == newNote.latitude && oldNote.longitude == newNote.longitude && oldNote.elevasi == newNote.elevasi && oldNote.sk == newNote.sk
    }
}