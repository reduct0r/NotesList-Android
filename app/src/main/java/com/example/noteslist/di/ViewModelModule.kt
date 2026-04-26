package com.example.noteslist.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.noteslist.presentation.notesList.NoteListViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

// Ключ для VM без runtime зависимостей
@MapKey
annotation class ViewModelKey(
    val value: KClass<out ViewModel>
)

@Module
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(NoteListViewModel::class)
    fun bindNoteListViewModel(
        vm: NoteListViewModel
    ): ViewModel

    companion object {
        @Provides
        fun provideFactory(factory: AppViewModelFactory): ViewModelProvider.Factory = factory
    }
}
