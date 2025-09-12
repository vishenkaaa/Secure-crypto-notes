package com.example.domain.usecase.note

import com.example.domain.model.note.Note
import com.example.domain.repository.NoteRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor (
    private val noteRepository: NoteRepository
){
    suspend operator fun invoke(note: Note): Result<Unit>  =
        try {
            noteRepository.deleteNote(note)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
}