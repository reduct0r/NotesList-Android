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
        SettingsModule::class,
        PlatformModule::class
    ]
)
interface AppComponent {

    fun inject(app: NoteListApp)
    fun inject(activity: com.example.noteslist.presentation.MainActivity)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance appContext: Context
        ): AppComponent
    }

    fun noteDetailsComponent(): NoteDetailsComponent.Factory
    fun noteListComponent(): NoteListComponent.Factory
    fun settingsComponent(): SettingsComponent.Factory
}

