package com.example.noteslist.di

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        RepositoryModule::class,
        ViewModelInitializerModule::class
    ]
)
interface AppComponent