package com.example.noteslist.di

import android.content.Context
import com.example.noteslist.presentation.noteDetails.NoteDetailsFragment
import com.example.noteslist.presentation.notesList.NoteListFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        RepositoryModule::class,
        ViewModelModule::class,
        UseCaseModule::class
    ]
)
interface AppComponent {
    fun inject(fragment: NoteListFragment)
    fun inject(fragment: NoteDetailsFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance appContext: Context
        ): AppComponent
    }
}
