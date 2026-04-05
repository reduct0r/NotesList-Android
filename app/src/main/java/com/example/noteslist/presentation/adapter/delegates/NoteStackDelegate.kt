package com.example.noteslist.presentation.adapter.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.databinding.ItemNoteStackBinding
import com.example.noteslist.domain.model.list.ListItem
import com.example.noteslist.domain.model.list.NoteStackItem

class NoteStackDelegate : AdapterDelegate<ListItem> {

    override fun isForViewType(items: List<ListItem>, position: Int): Boolean =
        items[position] is NoteStackItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        NoteStackViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        items: List<ListItem>,
        position: Int
    ) {
        val item = items[position] as NoteStackItem
        (holder as NoteStackViewHolder).bind(item)
    }

    private class NoteStackViewHolder(
        private val binding: ItemNoteStackBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NoteStackItem) {
            with(binding.noteStack) { setNotes(item.notes) }
        }

        companion object {
            fun create(parent: ViewGroup): NoteStackViewHolder {
                val binding = ItemNoteStackBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return NoteStackViewHolder(binding)
            }
        }
    }
}