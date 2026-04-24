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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailsScreen(
    viewModel: NoteDetailsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                NoteDetailsViewModel.UiEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    NoteDetailsScreenScaffold(
        uiState = uiState,
        isLandscape = isLandscape,
        onBackPressed = viewModel::onBackPressedRequested,
        onTitleChanged = viewModel::onTitleChanged,
        onContentChanged = viewModel::onContentChanged,
        onImportantChanged = viewModel::onImportantChanged,
        onReadChanged = viewModel::onReadChanged,
        onSave = viewModel::onSaveClicked,
        onDismissUnsavedDialog = viewModel::dismissUnsavedDialog,
        onConfirmUnsavedClose = viewModel::confirmCloseWithoutSaving
    )
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
    NoteDetailsContentPreview(isNew = true)
}

@NoteDetailsMultiPreview
@Composable
private fun NoteDetailsScreenExistingPreview() {
    NoteDetailsContentPreview(isNew = false)
}

@Composable
private fun NoteDetailsContentPreview(isNew: Boolean) {
    val note = if (isNew) null else Note(
        id = 101L,
        title = "Сходить в магазин",
        content = "Купить молоко и хлеб",
        createdAt = System.currentTimeMillis(),
        isImportant = true,
        isRead = false
    )

    val previewState = NoteDetailsUiState(
        currentNote = note,
        title = note?.title.orEmpty(),
        content = note?.content.orEmpty(),
        isImportant = note?.isImportant ?: false,
        isRead = note?.isRead ?: false,
        isNewNote = isNew
    )

    NoteDetailsScreenScaffold(
        uiState = previewState,
        isLandscape = false,
        onBackPressed = {},
        onTitleChanged = {},
        onContentChanged = {},
        onImportantChanged = {},
        onReadChanged = {},
        onSave = {},
        onDismissUnsavedDialog = {},
        onConfirmUnsavedClose = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteDetailsScreenScaffold(
    uiState: NoteDetailsUiState,
    isLandscape: Boolean,
    onBackPressed: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onImportantChanged: (Boolean) -> Unit,
    onReadChanged: (Boolean) -> Unit,
    onSave: () -> Unit,
    onDismissUnsavedDialog: () -> Unit,
    onConfirmUnsavedClose: () -> Unit
) {
    BackHandler(enabled = true) {
        onBackPressed()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isNewNote) stringResource(R.string.note_details_title_new)
                        else stringResource(R.string.note_details_title_existing)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
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
                        value = uiState.title,
                        onValueChange = onTitleChanged,
                        label = { Text(stringResource(R.string.note_title_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = uiState.title.isBlank(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        singleLine = true
                    )

                    if (uiState.title.isBlank()) {
                        Text(
                            text = stringResource(R.string.note_title_required_error),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    OutlinedTextField(
                        value = uiState.content,
                        onValueChange = onContentChanged,
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
                            checked = uiState.isImportant,
                            onCheckedChange = onImportantChanged
                        )
                        Spacer(Modifier.width(UiDimen.CHECKBOX_TEXT_SPACER))
                        Text(stringResource(R.string.note_important_label))
                    }

                    if (!uiState.isNewNote) {
                        Text(
                            text = stringResource(
                                R.string.note_created_format,
                                uiState.currentNote?.getDateString().orEmpty(),
                                uiState.currentNote?.getTimeString().orEmpty()
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = uiState.isRead,
                                onCheckedChange = onReadChanged
                            )
                            Spacer(Modifier.width(UiDimen.CHECKBOX_TEXT_SPACER))
                            Text(stringResource(R.string.note_read_label))
                        }
                    }

                    Spacer(Modifier.weight(UiDimen.BOTTOM_SPACER_WEIGHT))

                    Button(
                        onClick = onSave,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (uiState.isNewNote) stringResource(R.string.add) else stringResource(R.string.save))
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
                    value = uiState.title,
                    onValueChange = onTitleChanged,
                    label = { Text(stringResource(R.string.note_title_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.title.isBlank(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    singleLine = true
                )

                if (uiState.title.isBlank()) {
                    Text(
                        text = stringResource(R.string.note_title_required_error),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = uiState.content,
                    onValueChange = onContentChanged,
                    label = { Text(stringResource(R.string.note_content_label)) },
                    modifier = Modifier.fillMaxWidth().height(UiDimen.PORTRAIT_CONTENT_HEIGHT),
                    maxLines = UiDimen.CONTENT_MAX_LINES,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = uiState.isImportant,
                        onCheckedChange = onImportantChanged
                    )
                    Spacer(Modifier.width(UiDimen.CHECKBOX_TEXT_SPACER))
                    Text(stringResource(R.string.note_important_label))
                }

                if (!uiState.isNewNote) {
                    Text(
                        text = stringResource(
                            R.string.note_created_format,
                            uiState.currentNote?.getDateString().orEmpty(),
                            uiState.currentNote?.getTimeString().orEmpty()
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = uiState.isRead,
                            onCheckedChange = onReadChanged
                        )
                        Spacer(Modifier.width(UiDimen.CHECKBOX_TEXT_SPACER))
                        Text(stringResource(R.string.note_read_label))
                    }
                }

                Spacer(Modifier.weight(UiDimen.BOTTOM_SPACER_WEIGHT))

                Button(
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (uiState.isNewNote) stringResource(R.string.add) else stringResource(R.string.save))
                }
            }
        }
    }

    if (uiState.showUnsavedDialog) {
        AlertDialog(
            onDismissRequest = onDismissUnsavedDialog,
            title = { Text(stringResource(R.string.unsaved_changes_title)) },
            text = { Text(stringResource(R.string.unsaved_changes_message)) },
            confirmButton = {
                TextButton(onClick = onConfirmUnsavedClose) {
                    Text(stringResource(R.string.close))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissUnsavedDialog) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

