package com.example.noteslist

import android.app.Application
import com.example.noteslist.di.AppComponent
import com.example.noteslist.di.DaggerAppComponent

class NoteListApp : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .factory()
            .create(this)
    }
}
