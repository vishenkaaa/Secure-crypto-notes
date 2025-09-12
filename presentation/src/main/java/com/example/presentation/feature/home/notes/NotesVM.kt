package com.example.presentation.feature.home.notes

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.viewModelScope
import com.example.domain.model.note.Note
import com.example.domain.usecase.note.AddNoteUseCase
import com.example.domain.usecase.note.DeleteNoteUseCase
import com.example.domain.usecase.note.GetAllNotesUseCase
import com.example.presentation.R
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.feature.home.notes.model.NotesUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class NotesVM @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val addNoteUseCase: AddNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(NotesUIState())
    val uiState: StateFlow<NotesUIState> = _uiState.asStateFlow()

    var scrollState = LazyListState()
        private set

    fun updateScrollState(newState: LazyListState) {
        scrollState = newState
    }

    init {
        observeNotes()
    }

    private fun observeNotes() {
        getAllNotesUseCase()
            .onStart { handleLoading(true) }
            .catch { e -> handleError(e)
                handleLoading(false)
            }
            .onEach { notes ->
                _uiState.update {
                    it.copy(
                        groupedNotes = notes.groupBy { n -> n.createdAt.toLocalDate() }
                    )
                }
                handleLoading(false)
            }.launchIn(viewModelScope)
    }

    private fun addNote() {
        val title = _uiState.value.title
        val content = _uiState.value.content

        viewModelScope.launch {
            try {
                handleLoading(true)
                addNoteUseCase(title, content)
                _uiState.update { it.copy(isAddBottomSheetOpen = false) }
            } catch (e: Throwable) {
                handleError(e) { addNote() }
            } finally {
                handleLoading(false)
            }
        }
    }

    fun requestDeleteConfirmation(note: Note) {
        _uiState.update {
            it.copy(
                showDeleteDialog = true,
                noteToDelete = note
            )
        }
    }

    fun onDeleteConfirmationResult(status: Boolean) {
        if (status) uiState.value.noteToDelete?.let{deleteNote(it)}
        _uiState.update {
            it.copy(
                showDeleteDialog = false,
                noteToDelete = null
            )
        }
    }

    private fun deleteNote(note: Note) {
        viewModelScope.launch {
            try {
                handleLoading(true)
                deleteNoteUseCase(note)
            } catch (e: Throwable) {
                handleError(e) { deleteNote(note) }
            } finally {
                handleLoading(false)
            }
        }
    }

    fun copyNote(note: Note, context: Context){
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(note.title, "${note.title}\n${note.content}")
        clipboard.setPrimaryClip(clip)

        Toast.makeText(context, context.getString(R.string.note_is_copied), Toast.LENGTH_SHORT).show()
    }

    fun openAddNoteBottomSheet() {
        _uiState.update { it.copy(isAddBottomSheetOpen = true) }
    }

    fun closeAddNoteBottomSheet() {
        _uiState.update { it.copy(isAddBottomSheetOpen = false) }
    }

    fun updateTitleNewNote(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun updateContentNewNote(newContent: String) {
        _uiState.update { it.copy(content = newContent) }
    }

    fun saveNewNote(){
        if (_uiState.value.title.isNotBlank() && _uiState.value.content.isNotBlank()) {
            addNote()
            clearNewNote()
        }
    }

    fun clearNewNote(){
        _uiState.update {
            it.copy(
                title = "",
                content = ""
            )
        }
    }

    fun openViewNoteBottomSheet(note: Note) {
        _uiState.update { it.copy(noteToView = note, isNoteBottomSheetOpen = true) }
    }

    fun closeViewNoteBottomSheet() {
        _uiState.update { it.copy(noteToView = null, isNoteBottomSheetOpen = false) }
    }

    private fun Long.toLocalDate(): LocalDate =
        Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
}