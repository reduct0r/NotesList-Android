package com.example.noteslist.di

import com.example.noteslist.data.repository.NoteRepositoryImpl
import com.example.noteslist.domain.repository.NoteRepository
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {

    @Binds
    fun bindNoteRepository(
        impl: NoteRepositoryImpl
    ): NoteRepository
}