package com.example.noteslist.di

import android.content.Context
import com.example.noteslist.presentation.notesList.NoteListFragment
import dagger.BindsInstance
import dagger.Component
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        RepositoryModule::class,
        ViewModelInitializerModule::class
    ]
)
interface AppComponent {
    fun inject(fragment: NoteListFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance appContext: Context
        ): AppComponent
    }

}