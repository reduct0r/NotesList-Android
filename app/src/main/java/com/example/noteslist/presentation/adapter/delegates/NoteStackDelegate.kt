package com.example.noteslist.presentation.adapter.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.databinding.ItemNoteStackBinding
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.list.ListItem
import com.example.noteslist.domain.model.list.NoteStackItem

class NoteStackDelegate(
    private val onNoteClick: (Note) -> Unit,
    private val onExpand: (NoteStackItem) -> Unit,
    private val onCollapse: (NoteStackItem) -> Unit
) : AdapterDelegate<ListItem> {

    override fun isForViewType(items: List<ListItem>, position: Int): Boolean =
        items[position] is NoteStackItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemNoteStackBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NoteStackViewHolder(binding, onNoteClick, onExpand, onCollapse)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        items: List<ListItem>,
        position: Int
    ) {
        val item = items[position] as NoteStackItem
        (holder as NoteStackViewHolder).bind(item)
    }

    private class NoteStackViewHolder(
        private val binding: ItemNoteStackBinding,
        private val onNoteClick: (Note) -> Unit,
        private val onExpand: (NoteStackItem) -> Unit,
        private val onCollapse: (NoteStackItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NoteStackItem) {
            binding.noteStack.apply {
                setNotes(item.notes, item.isExpanded, onNoteClick)

                onExpandRequest = {
                    onExpand(item)
                }

                onCollapseRequest = {
                    onCollapse(item)
                }
            }
        }
    }
}