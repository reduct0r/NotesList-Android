package com.example.noteslist.presentation.adapter.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.databinding.ItemImportantNoteBinding
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.getTimeString
import com.example.noteslist.domain.model.list.ImportantNoteItem
import com.example.noteslist.domain.model.list.ListItem

class ImportantNoteDelegate(
    private val onClick: (Note) -> Unit
) : AdapterDelegate<ListItem> {

    override fun isForViewType(items: List<ListItem>, position: Int): Boolean =
        items[position] is ImportantNoteItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemImportantNoteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImportantNoteViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        items: List<ListItem>,
        position: Int
    ) {
        val item = items[position] as ImportantNoteItem
        (holder as ImportantNoteViewHolder).bind(item)
    }

    private class ImportantNoteViewHolder(
        private val binding: ItemImportantNoteBinding,
        private val onClick: (Note) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ImportantNoteItem) {
            val note = item.note
            binding.noteView.setOnClickListener { onClick(note) }

            with(binding.noteView) {
                title = note.title
                content = note.content
                time = note.getTimeString()
                isImportant = note.isImportant
                isRead = note.isRead
            }
        }
    }
}