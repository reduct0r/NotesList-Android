package com.example.noteslist.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.list.ImportantNoteItem
import com.example.noteslist.domain.model.list.ListItem
import com.example.noteslist.domain.model.list.NoteStackItem

class NoteListAdapter(
    private val onNoteClick: (Note) -> Unit
) : ListAdapter<ListItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ImportantNoteItem -> VIEW_TYPE_IMPORTANT
            is NoteStackItem -> VIEW_TYPE_STACK
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_IMPORTANT -> ImportantNoteViewHolder.create(parent, onNoteClick)
            VIEW_TYPE_STACK -> NoteStackViewHolder.create(parent)
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ImportantNoteItem -> (holder as ImportantNoteViewHolder).bind(item)
            is NoteStackItem -> (holder as NoteStackViewHolder).bind(item)
        }
    }

    companion object {
        private const val VIEW_TYPE_IMPORTANT = 1
        private const val VIEW_TYPE_STACK = 2
    }

    private class DiffCallback : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return when {
                oldItem is ImportantNoteItem && newItem is ImportantNoteItem ->
                    oldItem.note.id == newItem.note.id
                oldItem is NoteStackItem && newItem is NoteStackItem ->
                    oldItem.notes.map { it.id } == newItem.notes.map { it.id }
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean = oldItem == newItem
    }
}