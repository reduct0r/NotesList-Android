package com.example.noteslist.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
interface SettingsScreenModule {

    @Binds
    fun bindViewModelFactory(
        factory: NoteListScreenViewModelFactory
    ): ViewModelProvider.Factory
}
