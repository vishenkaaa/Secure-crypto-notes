package com.example.domain.repository

import com.example.domain.model.note.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun addNote(title: String, content: String)
    suspend fun deleteNote(note: Note)
}
