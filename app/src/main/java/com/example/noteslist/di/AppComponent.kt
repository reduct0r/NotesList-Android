package com.example.noteslist.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        RepositoryModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance appContext: Context
        ): AppComponent
    }

    fun noteDetailsComponent(): NoteDetailsComponent.Factory
    fun noteListComponent(): NoteListComponent.Factory
}

