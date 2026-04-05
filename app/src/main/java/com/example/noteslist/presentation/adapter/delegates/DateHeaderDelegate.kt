package com.example.noteslist.presentation.adapter.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.databinding.ItemDateHeaderBinding
import com.example.noteslist.domain.model.list.DateHeaderItem
import com.example.noteslist.domain.model.list.ListItem

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

    private class DateHeaderViewHolder(
        private val binding: ItemDateHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DateHeaderItem) {
            with(binding.tvDate) {
                text = item.date
            }
        }

        companion object {
            fun create(parent: ViewGroup): DateHeaderViewHolder {
                val binding = ItemDateHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return DateHeaderViewHolder(binding)
            }
        }
    }
}