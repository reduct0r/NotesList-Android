package com.example.noteslist.di

import com.example.noteslist.presentation.noteDetails.NoteDetailsFragment
import dagger.Subcomponent

@ScreenScope
@Subcomponent
interface NoteDetailsComponent {

    fun inject(fragment: NoteDetailsFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): NoteDetailsComponent
    }
}