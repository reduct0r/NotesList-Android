package com.example.noteslist.presentation.adapter.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.databinding.ItemImportantNoteBinding
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.list.ImportantNoteItem

class ImportantNoteViewHolder(
    private val binding: ItemImportantNoteBinding,
    private val onClick: (Note) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ImportantNoteItem) {
        val note = item.note
        with(binding.noteView) {
            title = note.title
            content = note.content
            time = note.getTimeString()
            isImportant = note.isImportant
            isRead = note.isRead
        }
    }


    companion object {
        fun create(
            parent: ViewGroup,
            onClick: (Note) -> Unit
        ): ImportantNoteViewHolder {
            val binding = ItemImportantNoteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ImportantNoteViewHolder(binding, onClick)
        }
    }
}