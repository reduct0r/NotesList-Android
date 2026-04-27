package com.example.noteslist.di

import android.content.Context
import com.example.noteslist.NoteListApp
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CoroutineModule::class,
        RepositoryModule::class,
        DatabaseModule::class,
        SettingsModule::class
    ]
)
interface AppComponent {

    fun inject(app: NoteListApp)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance appContext: Context
        ): AppComponent
    }

    fun noteDetailsComponent(): NoteDetailsComponent.Factory
    fun noteListComponent(): NoteListComponent.Factory
}

