package com.example.noteslist.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
}