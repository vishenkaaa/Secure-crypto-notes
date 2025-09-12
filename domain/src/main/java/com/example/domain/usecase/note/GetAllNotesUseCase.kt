package com.example.domain.usecase.note

import com.example.domain.model.Note
import com.example.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllNotesUseCase @Inject constructor (
    private val noteRepository: NoteRepository
){
    operator fun invoke(): Flow<List<Note>> =
        noteRepository.getAllNotes()
}