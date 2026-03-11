package com.example.noteslist.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.list.ImportantNoteItem
import com.example.noteslist.domain.model.list.ListItem

class ImportantNoteDelegate(
    private val onClick: (Note) -> Unit
) : AdapterDelegate<ListItem> {

    override fun isForViewType(items: List<ListItem>, position: Int): Boolean =
        items[position] is ImportantNoteItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        ImportantNoteViewHolder.create(parent, onClick)

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        items: List<ListItem>,
        position: Int
    ) {
        val item = items[position] as ImportantNoteItem
        (holder as ImportantNoteViewHolder).bind(item)
    }
}