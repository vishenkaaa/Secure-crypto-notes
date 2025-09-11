package com.example.domain.usecase.note

import com.example.domain.repository.NoteRepository
import javax.inject.Inject

class AddNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(title: String, content: String): Result<Unit> =
        try {
            noteRepository.addNote(title, content)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
}