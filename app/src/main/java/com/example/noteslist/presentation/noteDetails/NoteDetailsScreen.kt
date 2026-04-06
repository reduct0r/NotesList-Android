package com.example.noteslist.presentation.noteDetails

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.noteslist.R
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.getDateString
import com.example.noteslist.domain.model.getTimeString
import com.example.noteslist.domain.model.isNew

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailsScreen(
    initialNote: Note?,
    viewModel: NoteDetailsViewModel? = null,
    onNavigateBack: () -> Unit
) {
    val noteKey = initialNote?.id ?: NEW_NOTE_KEY
    val isNewNote = initialNote?.isNew() ?: true
    var currentNote by rememberSaveable(noteKey) { mutableStateOf(initialNote) }
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    var title by rememberSaveable(noteKey) { mutableStateOf(initialNote?.title ?: "") }
    var content by rememberSaveable(noteKey) { mutableStateOf(initialNote?.content ?: "") }
    var isImportant by rememberSaveable(noteKey) { mutableStateOf(initialNote?.isImportant ?: false) }
    var isRead by rememberSaveable(noteKey) { mutableStateOf(initialNote?.isRead ?: false) }
    var isReadManuallyEdited by rememberSaveable(noteKey) { mutableStateOf(false) }

    var showUnsavedDialog by rememberSaveable(noteKey) { mutableStateOf(false) }

    val hasChanges = remember(title, content, isImportant, isRead, currentNote) {
        title != (currentNote?.title ?: "") ||
                content != (currentNote?.content ?: "") ||
                isImportant != (currentNote?.isImportant ?: false) ||
                isRead != (currentNote?.isRead ?: false)
    }

    fun saveNote() {
        if (title.isBlank()) return

        val noteToSave = (currentNote ?: Note()).copy(
            title = title.trim(),
            content = content.trim(),
            isImportant = isImportant,
            isRead = isRead
        )

        viewModel?.saveNote(noteToSave)
    }

    fun buildDraftNote(): Note {
        return (currentNote ?: Note()).copy(
            title = title,
            content = content,
            isImportant = isImportant,
            isRead = isRead
        )
    }

    LaunchedEffect(viewModel) {
        val currentViewModel = viewModel ?: return@LaunchedEffect
        currentViewModel.events.collect { event ->
            when (event) {
                NoteDetailsViewModel.UiEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    LaunchedEffect(initialNote?.id, viewModel) {
        val currentViewModel = viewModel ?: return@LaunchedEffect
        val noteId = initialNote?.takeIf { !it.isNew() }?.id ?: return@LaunchedEffect
        currentViewModel.observeNote(noteId).collect { updatedNote ->
            currentNote = updatedNote
            if (updatedNote != null && !isReadManuallyEdited) {
                isRead = updatedNote.isRead
            }
        }
    }

    LaunchedEffect(title, content, isImportant, isRead, currentNote, viewModel) {
        viewModel?.updateDraft(buildDraftNote())
    }

    BackHandler(enabled = true) {
        if (hasChanges) {
            showUnsavedDialog = true
        } else {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isNewNote) stringResource(R.string.note_details_title_new)
                        else stringResource(R.string.note_details_title_existing)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (hasChanges) showUnsavedDialog = true
                        else onNavigateBack()
                    }) {
                        Icon(
                            painterResource(R.drawable.back),
                            contentDescription = stringResource(R.string.back_description)
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(UiDimen.SCREEN_PADDING)
            ) {
                Column(
                    modifier = Modifier
                        .weight(UiDimen.LANDSCAPE_LEFT_WEIGHT)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(UiDimen.LANDSCAPE_SPACING)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(stringResource(R.string.note_title_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = title.isBlank(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        singleLine = true
                    )

                    if (title.isBlank()) {
                        Text(
                            text = stringResource(R.string.note_title_required_error),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text(stringResource(R.string.note_content_label)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(UiDimen.CONTENT_FIELD_WEIGHT),
                        maxLines = UiDimen.CONTENT_MAX_LINES,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                }

                Spacer(Modifier.width(UiDimen.LANDSCAPE_COLUMN_SPACER))

                Column(
                    modifier = Modifier
                        .weight(UiDimen.LANDSCAPE_RIGHT_WEIGHT)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(UiDimen.LANDSCAPE_SPACING)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isImportant,
                            onCheckedChange = { isImportant = it }
                        )
                        Spacer(Modifier.width(UiDimen.CHECKBOX_TEXT_SPACER))
                        Text(stringResource(R.string.note_important_label))
                    }

                    if (!isNewNote) {
                        Text(
                            text = stringResource(
                                R.string.note_created_format,
                                currentNote?.getDateString().orEmpty(),
                                currentNote?.getTimeString().orEmpty()
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isRead,
                                onCheckedChange = {
                                    isRead = it
                                    isReadManuallyEdited = true
                                }
                            )
                            Spacer(Modifier.width(UiDimen.CHECKBOX_TEXT_SPACER))
                            Text(stringResource(R.string.note_read_label))
                        }
                    }

                    Spacer(Modifier.weight(UiDimen.BOTTOM_SPACER_WEIGHT))

                    Button(
                        onClick = { saveNote() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isNewNote) stringResource(R.string.add) else stringResource(R.string.save))
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(UiDimen.SCREEN_PADDING)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(UiDimen.PORTRAIT_SPACING)
            ) {

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.note_title_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = title.isBlank(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    singleLine = true
                )

                if (title.isBlank()) {
                    Text(
                        text = stringResource(R.string.note_title_required_error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text(stringResource(R.string.note_content_label)) },
                    modifier = Modifier.fillMaxWidth().height(UiDimen.PORTRAIT_CONTENT_HEIGHT),
                    maxLines = UiDimen.CONTENT_MAX_LINES,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isImportant,
                        onCheckedChange = { isImportant = it }
                    )
                    Spacer(Modifier.width(UiDimen.CHECKBOX_TEXT_SPACER))
                    Text(stringResource(R.string.note_important_label))
                }

                if (!isNewNote) {
                    Text(
                        text = stringResource(
                            R.string.note_created_format,
                            currentNote?.getDateString().orEmpty(),
                            currentNote?.getTimeString().orEmpty()
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isRead,
                            onCheckedChange = {
                                isRead = it
                                isReadManuallyEdited = true
                            }
                        )
                        Spacer(Modifier.width(UiDimen.CHECKBOX_TEXT_SPACER))
                        Text(stringResource(R.string.note_read_label))
                    }
                }

                Spacer(Modifier.weight(UiDimen.BOTTOM_SPACER_WEIGHT))

                Button(
                    onClick = { saveNote() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isNewNote) stringResource(R.string.add) else stringResource(R.string.save))
                }
            }
        }
    }

    if (showUnsavedDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedDialog = false },
            title = { Text(stringResource(R.string.unsaved_changes_title)) },
            text = { Text(stringResource(R.string.unsaved_changes_message)) },
            confirmButton = {
                TextButton(onClick = { onNavigateBack() }) {
                    Text(stringResource(R.string.close))
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnsavedDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

private object UiDimen {
    val SCREEN_PADDING = 16.dp
    val PORTRAIT_SPACING = 16.dp
    val LANDSCAPE_SPACING = 12.dp
    val CHECKBOX_TEXT_SPACER = 8.dp
    val LANDSCAPE_COLUMN_SPACER = 12.dp
    val PORTRAIT_CONTENT_HEIGHT = 200.dp

    const val CONTENT_MAX_LINES = 10
    const val CONTENT_FIELD_WEIGHT = 1f
    const val BOTTOM_SPACER_WEIGHT = 1f
    const val LANDSCAPE_LEFT_WEIGHT = 1.2f
    const val LANDSCAPE_RIGHT_WEIGHT = 0.8f
}

private const val NEW_NOTE_KEY = -1L

@Preview(
    name = "Portrait",
    showBackground = true,
    widthDp = 360,
    heightDp = 740
)
@Preview(
    name = "Landscape",
    showBackground = true,
    widthDp = 740,
    heightDp = 360
)
private annotation class NoteDetailsMultiPreview

@NoteDetailsMultiPreview
@Composable
private fun NoteDetailsScreenNewPreview() {
    NoteDetailsScreen(
        initialNote = null,
        onNavigateBack = {}
    )
}

@NoteDetailsMultiPreview
@Composable
private fun NoteDetailsScreenExistingPreview() {
    NoteDetailsScreen(
        initialNote = Note(
            id = 101L,
            title = "Сходить в магазин",
            content = "Купить молоко и хлеб",
            isImportant = true,
            isRead = false
        ),
        onNavigateBack = {}
    )
}

