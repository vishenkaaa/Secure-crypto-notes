package com.example.data.repositories

import com.example.data.local.db.dao.NoteDao
import com.example.data.local.db.entities.NoteEntity
import com.example.data.local.db.entities.toDomain
import com.example.data.local.db.entities.toEntity
import com.example.domain.model.note.Note
import com.example.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {
    override fun getAllNotes(): Flow<List<Note>> =
        noteDao.getAllNotes().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun addNote(title: String, content: String) {
        noteDao.insert(NoteEntity(title = title, content = content))
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.delete(note.toEntity())
    }
}
