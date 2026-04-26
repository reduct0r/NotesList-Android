package com.example.noteslist.data.repository

import com.example.noteslist.data.local.dao.NoteDao
import com.example.noteslist.data.mapper.toDomain
import com.example.noteslist.data.mapper.toEntity
import com.example.noteslist.domain.repository.NoteRepository
import com.example.noteslist.domain.model.Note
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class  NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
): NoteRepository {

    override val notes: Flow<List<Note>> =
        noteDao.getAll()
            .map { list ->
                list.map { it.toDomain() }
            }

    override suspend fun addNote(note: Note) {
        noteDao.insert(note.toEntity())
    }

    override suspend fun updateNote(note: Note) {
        noteDao.update(note.toEntity())
    }

    override suspend fun updateReadStatus(note: Note) {
        noteDao.updateReadStatus(note.id.toString(), !note.isRead)
    }

    override suspend fun updateImportantStatus(note: Note) {
        noteDao.updateImportantStatus(note.id.toString(), !note.isImportant)
    }

}
