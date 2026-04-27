package com.example.noteslist.data.repository

import com.example.noteslist.data.local.dao.NoteDao
import com.example.noteslist.data.mapper.toDomain
import com.example.noteslist.data.mapper.toEntity
import com.example.noteslist.domain.common.IdGenerator
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val idGenerator: IdGenerator
) : NoteRepository {

    override val notes: Flow<List<Note>> =
        noteDao.getAll()
            .map { list ->
                list.map { it.toDomain() }
            }

    override suspend fun addNote(note: Note) {
        val noteWithId = note.id?.let { note } ?: note.copy(id = idGenerator.randomUuid())
        noteDao.insert(noteWithId.toEntity())
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
