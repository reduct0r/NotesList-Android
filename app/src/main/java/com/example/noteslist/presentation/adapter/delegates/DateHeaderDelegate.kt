package com.example.noteslist.presentation.adapter.delegates

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.domain.model.list.DateHeaderItem
import com.example.noteslist.domain.model.list.ListItem
import com.example.noteslist.presentation.adapter.viewHolder.DateHeaderViewHolder

class DateHeaderDelegate : AdapterDelegate<ListItem> {

    override fun isForViewType(items: List<ListItem>, position: Int): Boolean =
        items[position] is DateHeaderItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        DateHeaderViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        items: List<ListItem>,
        position: Int
    ) {
        val item = items[position] as DateHeaderItem
        (holder as DateHeaderViewHolder).bind(item)
    }
}