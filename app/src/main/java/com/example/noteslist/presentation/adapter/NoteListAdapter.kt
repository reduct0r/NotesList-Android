package com.example.noteslist.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.domain.model.list.ImportantNoteItem
import com.example.noteslist.domain.model.list.ListItem
import com.example.noteslist.domain.model.list.NoteStackItem
import com.example.noteslist.presentation.adapter.delegates.ImportantNoteDelegate
import com.example.noteslist.presentation.adapter.delegates.NoteStackDelegate

class NoteListAdapter(
    importantNoteDelegate: ImportantNoteDelegate
) : ListAdapter<ListItem, RecyclerView.ViewHolder>(DiffCallback()) {

    private val delegates = listOf(
        importantNoteDelegate,
        NoteStackDelegate()
    )

    override fun getItemViewType(position: Int): Int {
        return delegates.indexOfFirst { it.isForViewType(currentList, position) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegates[viewType].onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegates[getItemViewType(position)].onBindViewHolder(holder, currentList, position)
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