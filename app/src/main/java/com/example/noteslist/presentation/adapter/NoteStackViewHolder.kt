package com.example.noteslist.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteslist.databinding.ItemNoteStackBinding
import com.example.noteslist.domain.model.list.NoteStackItem

class NoteStackViewHolder(
    private val binding: ItemNoteStackBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: NoteStackItem) {
        binding.noteStack.setNotes(item.notes)
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