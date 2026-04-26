package com.example.noteslist.di

import com.example.noteslist.presentation.noteDetails.NoteDetailsFragment
import dagger.BindsInstance
import dagger.Subcomponent
import java.util.UUID

@ScreenScope
@Subcomponent(modules = [NoteDetailsScreenModule::class])
interface NoteDetailsComponent {

    fun inject(fragment: NoteDetailsFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance noteId: UUID?
        ): NoteDetailsComponent
    }
}