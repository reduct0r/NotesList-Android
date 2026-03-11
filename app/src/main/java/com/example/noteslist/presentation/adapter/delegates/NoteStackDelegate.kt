package com.example.noteslist.presentation.adapter.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.domain.model.list.ListItem
import com.example.noteslist.domain.model.list.NoteStackItem
import com.example.noteslist.presentation.adapter.viewHolder.NoteStackViewHolder

class NoteStackDelegate : AdapterDelegate<ListItem> {

    override fun isForViewType(items: List<ListItem>, position: Int): Boolean =
        items[position] is NoteStackItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        NoteStackViewHolder.Companion.create(parent)

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        items: List<ListItem>,
        position: Int
    ) {
        val item = items[position] as NoteStackItem
        (holder as NoteStackViewHolder).bind(item)
    }
}