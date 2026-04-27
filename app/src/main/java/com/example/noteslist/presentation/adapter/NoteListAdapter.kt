package com.example.noteslist.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.list.DateHeaderItem
import com.example.noteslist.domain.model.list.ImportantNoteItem
import com.example.noteslist.domain.model.list.ListItem
import com.example.noteslist.domain.model.list.NoteStackItem
import com.example.noteslist.presentation.adapter.delegates.DateHeaderDelegate
import com.example.noteslist.presentation.adapter.delegates.ImportantNoteDelegate
import com.example.noteslist.presentation.adapter.delegates.NoteStackDelegate
import com.example.noteslist.presentation.notesList.StackSettings

class NoteListAdapter(
    private val onNoteClick: (Note) -> Unit,
    private val onNoteLongClick: (Note) -> Unit,
    private val onToggleStack: (NoteStackItem) -> Unit
) : ListAdapter<ListItem, RecyclerView.ViewHolder>(DiffCallback()) {

    private var stackSettings = StackSettings()

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private val delegates = listOf(
        DateHeaderDelegate(),
        ImportantNoteDelegate(onClick = onNoteClick, onLongClick = onNoteLongClick),
        NoteStackDelegate(
            stackSettingsProvider = { stackSettings },
            onNoteClick = onNoteClick,
            onNoteLongClick = onNoteLongClick,
            onToggleStack = onToggleStack
        )
    )

    fun updateStackSettings(settings: StackSettings) {
        if (stackSettings == settings) return
        stackSettings = settings
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return delegates.indexOfFirst { it.isForViewType(currentList, position) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegates[viewType].onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegates[holder.itemViewType].onBindViewHolder(holder, currentList, position)
    }

    private class DiffCallback : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return when {
                oldItem is ImportantNoteItem && newItem is ImportantNoteItem ->
                    oldItem.note.id == newItem.note.id

                oldItem is NoteStackItem && newItem is NoteStackItem ->
                    oldItem.notes.map { it.id } == newItem.notes.map { it.id }

                oldItem is DateHeaderItem && newItem is DateHeaderItem ->
                    oldItem.date == newItem.date

                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }
    }
}
