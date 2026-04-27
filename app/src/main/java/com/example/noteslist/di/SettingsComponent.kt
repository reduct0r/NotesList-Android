package com.example.noteslist.di

import com.example.noteslist.presentation.notesList.SettingsBottomSheet
import dagger.Subcomponent

@ScreenScope
@Subcomponent(modules = [SettingsScreenModule::class])
interface SettingsComponent {

    fun inject(fragment: SettingsBottomSheet)

    @Subcomponent.Factory
    interface Factory {
        fun create(): SettingsComponent
    }
}
