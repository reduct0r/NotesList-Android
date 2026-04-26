package com.example.noteslist.data.local.database

import com.example.noteslist.data.local.SeedData
import com.example.noteslist.data.local.dao.NoteDao
import javax.inject.Inject

class DatabaseSeeder @Inject constructor(
    private val dao: NoteDao
) {

    suspend fun seedIfNeeded() {
        if (dao.getCount() == 0) {
            dao.insertAll(SeedData.notes())
        }
    }
}