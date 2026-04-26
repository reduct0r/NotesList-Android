package com.example.noteslist.di

import com.example.noteslist.presentation.notesList.NoteListFragment
import dagger.Subcomponent

@ScreenScope
@Subcomponent(modules = [NoteListScreenModule::class])
interface NoteListComponent {

    fun inject(fragment: NoteListFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): NoteListComponent
    }
}