package com.example.noteslist.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
interface NoteDetailsScreenModule {

    @Binds
    fun bindViewModelFactory(
        factory: NoteDetailsScreenViewModelFactory
    ): ViewModelProvider.Factory
}
