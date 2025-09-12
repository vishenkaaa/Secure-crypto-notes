package com.example.presentation.feature.home.notes

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.Note
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.CenterAlignedHeader
import com.example.presentation.common.ui.components.ConfirmationDialog
import com.example.presentation.common.ui.components.HandleError
import com.example.presentation.common.ui.components.LoadingBackground
import com.example.presentation.common.ui.modifier.softShadow
import com.example.presentation.common.ui.values.Orange
import com.example.presentation.common.ui.values.SecureCryptoNotesTheme
import com.example.presentation.feature.home.notes.model.NotesUIState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@SuppressLint("FrequentlyChangedStateReadInComposition")
@Composable
fun NotesRoute(viewModel: NotesVM = hiltViewModel()) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiState.collectAsStateWithLifecycle()

    val listState = viewModel.scrollState

    var previousIndex by remember { mutableIntStateOf(0) }
    var previousScrollOffset by remember { mutableIntStateOf(0) }

    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        viewModel.updateScrollState(listState)
    }

    val isScrollingDown = remember {
        derivedStateOf {
            val currentIndex = listState.firstVisibleItemIndex
            val currentOffset = listState.firstVisibleItemScrollOffset
            val scrollingDown = when {
                currentIndex > previousIndex -> true
                currentIndex < previousIndex -> false
                else -> currentOffset > previousScrollOffset
            }
            previousIndex = currentIndex
            previousScrollOffset = currentOffset
            scrollingDown
        }
    }

    NotesScreen(
        uiState = uiState,
        baseUiState = baseUiState,
        listState = listState,
        fabVisible = !isScrollingDown.value,
        openAddNoteBottomSheet = viewModel::openAddNoteBottomSheet,
        closeAddNoteBottomSheet = viewModel::closeAddNoteBottomSheet,
        openViewNoteBottomSheet = viewModel::openViewNoteBottomSheet,
        closeViewNoteBottomSheet = viewModel::closeViewNoteBottomSheet,
        requestDeleteConfirmation = viewModel::requestDeleteConfirmation,
        onDeleteConfirmationResult = viewModel::onDeleteConfirmationResult,
        onCopyNote = viewModel::copyNote,
        clearNewNote = viewModel::clearNewNote,
        onSaveNewNote = viewModel::saveNewNote,
        updateNewNoteTitle = viewModel::updateTitleNewNote,
        updateNewNoteContent = viewModel::updateContentNewNote,
        clearErrors = viewModel::clearErrors,
        retryLastAction = viewModel::retryLastAction,
        hasRetryAction = viewModel.hasRetryAction()
    )
}

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    uiState: NotesUIState,
    baseUiState: BaseUiState,
    listState: LazyListState,
    fabVisible: Boolean,
    openAddNoteBottomSheet: () -> Unit,
    closeAddNoteBottomSheet: () -> Unit,
    openViewNoteBottomSheet: (Note) -> Unit,
    closeViewNoteBottomSheet: () -> Unit,
    requestDeleteConfirmation: (Note) -> Unit,
    onDeleteConfirmationResult: (Boolean) -> Unit,
    onCopyNote: (Note, Context) -> Unit,
    clearNewNote: () -> Unit,
    onSaveNewNote: () -> Unit,
    updateNewNoteTitle: (String) -> Unit,
    updateNewNoteContent: (String) -> Unit,
    clearErrors: () -> Unit,
    retryLastAction: () -> Unit,
    hasRetryAction: Boolean = false
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = { CenterAlignedHeader(stringResource(R.string.app_name)) },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = fabVisible,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    FloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.primary,
                        onClick = openAddNoteBottomSheet,
                        modifier = Modifier.padding(bottom = 80.dp),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add note"
                        )
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { padding ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val sortedGroups = uiState.groupedNotes.toList().sortedByDescending { it.first }

                items(sortedGroups) { (date, notes) ->
                    DateSection(
                        date = date,
                        notes = notes.sortedByDescending { it.createdAt },
                        onDeleteNote = requestDeleteConfirmation,
                        onCopyNote = onCopyNote,
                        onClick = openViewNoteBottomSheet
                    )
                }

                item { Spacer(Modifier.height(60.dp)) }
            }

            ConfirmationDialog(
                visible = uiState.showDeleteDialog,
                title = stringResource(R.string.delete_note_title),
                message = stringResource(R.string.delete_note_message),
                confirmButtonText = stringResource(R.string.delete),
                dismissButtonText = stringResource(R.string.cancel),
                onConfirm = { onDeleteConfirmationResult(true) },
                onDismiss = { onDeleteConfirmationResult(false) }
            )

            if (uiState.isAddBottomSheetOpen) {
                ModalBottomSheet(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    onDismissRequest = closeAddNoteBottomSheet
                ) {
                    AddNoteBottomSheet(
                        screenHeight = screenHeight,
                        title = uiState.title,
                        content = uiState.content,
                        onTitleChange = updateNewNoteTitle,
                        onContentChange = updateNewNoteContent,
                        onSave = onSaveNewNote,
                        onCancel = {
                            closeAddNoteBottomSheet()
                            clearNewNote()
                        }
                    )
                }
            }

            if (uiState.noteToView != null && uiState.isNoteBottomSheetOpen) {
                ModalBottomSheet(
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    containerColor = MaterialTheme.colorScheme.background,
                    onDismissRequest = closeViewNoteBottomSheet
                ) {
                    ViewNoteBottomSheet(
                        note = uiState.noteToView,
                        screenHeight = screenHeight
                    )
                }
            }
        }

        HandleError(
            baseUiState = baseUiState,
            onErrorConsume = clearErrors,
            onRetry = if (hasRetryAction) retryLastAction else null
        )

        LoadingBackground(baseUiState.isLoading)
    }
}

@Composable
private fun ViewNoteBottomSheet(note: Note, screenHeight: Dp) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .heightIn(max = screenHeight * 0.8f)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = note.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = note.content,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun formatDate(date: LocalDate): String {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    return when (date) {
        today -> stringResource(R.string.today)
        yesterday -> stringResource(R.string.yesterday)
        else -> {
            val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("en"))
            date.format(formatter)
        }
    }
}

@Composable
private fun DateSection(
    date: LocalDate,
    notes: List<Note>,
    onCopyNote: (Note, Context) -> Unit,
    onDeleteNote: (Note) -> Unit,
    onClick: (Note) -> Unit
) {
    val context = LocalContext.current
    Column {
        Text(
            text = formatDate(date),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        notes.forEach { note ->
            SwipeNoteItem(
                note = note,
                onCopy = { onCopyNote(note, context) },
                onDelete = { onDeleteNote(note) },
                onClick = onClick,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun SwipeNoteItem(
    note: Note,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    onClick: (Note) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        when (dismissState.currentValue) {
            SwipeToDismissBoxValue.EndToStart -> {
                onDelete()
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }
            SwipeToDismissBoxValue.StartToEnd -> {
                onCopy()
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }
            SwipeToDismissBoxValue.Settled -> Unit
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier.softShadow(
            color = MaterialTheme.colorScheme.surfaceVariant,
            blurRadius = 12.dp,
            offsetY = 1.dp,
            offsetX = 1.dp,
            cornerRadius = 16.dp
        ),
        backgroundContent = { SwipeBackground(dismissState = dismissState) },
        content = { NoteItem(note, onClick) }
    )
}

@Composable
private fun SwipeBackground(dismissState: SwipeToDismissBoxState) {
    val alignment = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        else -> Alignment.Center
    }

    val icon = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> painterResource(R.drawable.ic_copy)
        SwipeToDismissBoxValue.EndToStart -> painterResource(R.drawable.ic_trash)
        else -> null
    }

    val iconTint = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Orange
        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
        else -> Color.Unspecified
    }

    val scale by animateFloatAsState(
        targetValue = if (
            dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd ||
            dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart
        ) 1.3f else 1f,
        animationSpec = tween(400)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 24.dp),
        contentAlignment = alignment
    ) {
        icon?.let {
            Icon(
                painter = it,
                contentDescription = "Action",
                tint = iconTint,
                modifier = Modifier
                    .scale(scale)
                    .size(24.dp)
            )
        }
    }
}

@Composable
private fun NoteItem(note: Note, onClick: (Note) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick(note)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AddNoteBottomSheet(
    screenHeight: Dp,
    title: String,
    content: String,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.add_note),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        NoteTextField(
            value = title,
            onValueChange = onTitleChange,
            label = stringResource(R.string.title),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        NoteTextField(
            value = content,
            onValueChange = onContentChange,
            label = stringResource(R.string.content),
            minLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = screenHeight - 400.dp)
                .padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.primary
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                enabled = title.isNotBlank() && content.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = stringResource(R.string.save_note),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun NoteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(16.dp),
        label = {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        singleLine = singleLine,
        minLines = minLines,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun NotesScreenPreview() {
    val mockNotes = listOf(
        Note(
            id = 1,
            title = "Grocery List",
            content = "Milk, Bread, Eggs, Apples, Chicken breast, Rice, Pasta, Tomatoes",
            createdAt = System.currentTimeMillis()
        ),
        Note(
            id = 2,
            title = "Meeting Notes",
            content = "Discussed project timeline, assigned tasks to team members, scheduled next review for Friday",
            createdAt = System.currentTimeMillis() - 86400000
        ),
        Note(
            id = 3,
            title = "Book Ideas",
            content = "1. Science fiction about time travel\n2. Mystery novel set in small town\n3. Biography of famous scientist",
            createdAt = System.currentTimeMillis() - 86400000
        ),
        Note(
            id = 4,
            title = "Weekend Plans",
            content = "Visit the museum, have lunch at new restaurant, go for a walk in the park",
            createdAt = System.currentTimeMillis() - 172800000
        )
    )

    val groupedNotes = mockNotes.groupBy { note ->
        Instant.ofEpochMilli(note.createdAt).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    val mockUIState = NotesUIState(
        groupedNotes = groupedNotes,
        isAddBottomSheetOpen = false,
        showDeleteDialog = false,
        noteToDelete = null,
        noteToView = null,
        isNoteBottomSheetOpen = false,
        title = "",
        content = ""
    )

    SecureCryptoNotesTheme {
        NotesScreen(
            uiState = mockUIState,
            baseUiState = BaseUiState(),
            listState = LazyListState(),
            fabVisible = true,
            openAddNoteBottomSheet = {},
            closeAddNoteBottomSheet = {},
            openViewNoteBottomSheet = {},
            closeViewNoteBottomSheet = {},
            requestDeleteConfirmation = {},
            onDeleteConfirmationResult = {},
            onCopyNote = { _, _ -> },
            clearNewNote = {},
            onSaveNewNote = {},
            updateNewNoteTitle = {},
            updateNewNoteContent = {},
            clearErrors = {},
            retryLastAction = {},
            hasRetryAction = false
        )
    }
}