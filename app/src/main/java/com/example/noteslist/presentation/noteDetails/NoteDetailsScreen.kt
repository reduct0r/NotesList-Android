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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
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
    viewModel: NoteDetailsViewModel,
    onNavigateBack: () -> Unit
) {
    val isNewNote = initialNote?.isNew() ?: true
    var currentNote by remember { mutableStateOf(initialNote) }
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val title = remember { mutableStateOf(initialNote?.title ?: "") }
    val content = remember { mutableStateOf(initialNote?.content ?: "") }
    val isImportant = remember { mutableStateOf(initialNote?.isImportant ?: false) }
    val isRead = remember { mutableStateOf(initialNote?.isRead ?: false) }

    var showUnsavedDialog by remember { mutableStateOf(false) }

    val hasChanges = remember(title.value, content.value, isImportant.value, isRead.value, currentNote) {
        title.value != (currentNote?.title ?: "") ||
                content.value != (currentNote?.content ?: "") ||
                isImportant.value != (currentNote?.isImportant ?: false) ||
                isRead.value != (currentNote?.isRead ?: false)
    }

    fun saveNote() {
        if (title.value.isBlank()) return

        val noteToSave = (currentNote ?: Note()).copy(
            title = title.value.trim(),
            content = content.value.trim(),
            isImportant = isImportant.value,
            isRead = isRead.value
        )

        viewModel.saveNote(noteToSave)
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                NoteDetailsViewModel.UiEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    LaunchedEffect(initialNote?.id) {
        val noteId = initialNote?.takeIf { !it.isNew() }?.id ?: return@LaunchedEffect
        viewModel.observeNote(noteId).collect { updatedNote ->
            currentNote = updatedNote
            if (updatedNote != null) {
                isRead.value = updatedNote.isRead
            }
        }
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
                            painterResource(id = R.drawable.outline_book_24),
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
                        value = title.value,
                        onValueChange = { title.value = it },
                        label = { Text(stringResource(R.string.note_title_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = title.value.isBlank(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        singleLine = true
                    )

                    if (title.value.isBlank()) {
                        Text(
                            text = stringResource(R.string.note_title_required_error),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    OutlinedTextField(
                        value = content.value,
                        onValueChange = { content.value = it },
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
                            checked = isImportant.value,
                            onCheckedChange = { isImportant.value = it }
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
                                checked = isRead.value,
                                onCheckedChange = { isRead.value = it }
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
                    value = title.value,
                    onValueChange = { title.value = it },
                    label = { Text(stringResource(R.string.note_title_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = title.value.isBlank(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    singleLine = true
                )

                if (title.value.isBlank()) {
                    Text(
                        text = stringResource(R.string.note_title_required_error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = content.value,
                    onValueChange = { content.value = it },
                    label = { Text(stringResource(R.string.note_content_label)) },
                    modifier = Modifier.fillMaxWidth().height(UiDimen.PORTRAIT_CONTENT_HEIGHT),
                    maxLines = UiDimen.CONTENT_MAX_LINES,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isImportant.value,
                        onCheckedChange = { isImportant.value = it }
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
                            checked = isRead.value,
                            onCheckedChange = { isRead.value = it }
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

