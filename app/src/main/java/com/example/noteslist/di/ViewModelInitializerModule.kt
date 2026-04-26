package com.example.noteslist.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.ViewModelInitializer
import com.example.noteslist.presentation.notesList.NoteListViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Provider
import dagger.multibindings.IntoSet

@Module
object ViewModelInitializerModule {

    @Provides
    @IntoSet
    fun provideNoteListViewModelInitializer(
        provider: Provider<NoteListViewModel>
    ): ViewModelInitializer<*> {
        return ViewModelInitializer(NoteListViewModel::class) {
            provider.get()
        }
    }

    @Provides
    fun provideViewModelFactory(
        initializers: Set<@JvmSuppressWildcards ViewModelInitializer<*>>
    ): ViewModelProvider.Factory {
        return ViewModelProvider.Factory.from(*initializers.toTypedArray())
    }

}