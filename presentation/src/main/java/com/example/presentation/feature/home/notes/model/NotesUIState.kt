package com.example.presentation.feature.home.notes.model

import com.example.domain.model.Note
import java.time.LocalDate

data class NotesUIState(
    val groupedNotes: Map<LocalDate, List<Note>> = emptyMap(),
    val isAddBottomSheetOpen: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val noteToDelete: Note? = null,
    val noteToView: Note? = null,
    val isNoteBottomSheetOpen: Boolean = false,
    val title: String = "",
    var content: String = ""
)