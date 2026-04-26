package com.example.noteslist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.noteslist.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes")
    fun getAll(): Flow<List<NoteEntity>>

    @Query("UPDATE notes SET isRead = :isRead WHERE id = :id")
    suspend fun updateReadStatus(id: String, isRead: Boolean)

    @Query("UPDATE notes SET isImportant = :isImportant WHERE id = :id")
    suspend fun updateImportantStatus(id: String, isImportant: Boolean)

    @Update
    suspend fun update(note: NoteEntity)

    @Insert
    suspend fun insertAll(notes: List<NoteEntity>)

    @Insert
    suspend fun insert(notes: NoteEntity)
}
