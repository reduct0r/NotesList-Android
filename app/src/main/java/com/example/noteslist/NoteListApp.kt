package com.example.noteslist

import android.app.Application
import android.util.Log
import com.example.noteslist.data.local.database.DatabaseSeeder
import com.example.noteslist.di.ApplicationScope
import com.example.noteslist.di.AppComponent
import com.example.noteslist.di.DaggerAppComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class NoteListApp : Application() {

    lateinit var appComponent: AppComponent
        private set

    @Inject
    lateinit var seeder: DatabaseSeeder

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .factory()
            .create(this)
            .also {
                it.inject(this)
            }

        applicationScope.launch {
            try {
                seeder.seedIfNeeded()
            } catch (e: Exception) {
                Log.e("NoteListApp", "Database seeding failed", e)
            }
        }
    }
}
