package com.example.noteslist.presentation.noteDetails

import android.graphics.drawable.Drawable
import androidx.activity.compose.BackHandler
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
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
    viewModel: NoteDetailsViewModel,
    onNavigateBack: () -> Unit
) {
    val isNewNote = initialNote?.isNew() ?: true
    val title = remember { mutableStateOf(initialNote?.title ?: "") }
    val content = remember { mutableStateOf(initialNote?.content ?: "") }
    val isImportant = remember { mutableStateOf(initialNote?.isImportant ?: false) }
    val isRead = remember { mutableStateOf(initialNote?.isRead ?: false) }

    var showUnsavedDialog by remember { mutableStateOf(false) }
    var showExitConfirmDialog by remember { mutableStateOf(false) }

    val hasChanges = remember(title.value, content.value, isImportant.value, isRead.value) {
        title.value != (initialNote?.title ?: "") ||
                content.value != (initialNote?.content ?: "") ||
                isImportant.value != (initialNote?.isImportant ?: false) ||
                isRead.value != (initialNote?.isRead ?: false)
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                NoteDetailsViewModel.UiEvent.NavigateBack -> onNavigateBack()
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
                title = { Text(if (isNewNote) "Новая заметка" else "Детали заметки") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (hasChanges) showUnsavedDialog = true
                        else onNavigateBack()
                    }) {
                        Icon(painterResource(id = R.drawable.outline_book_24), contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = title.value,
                onValueChange = { title.value = it },
                label = { Text("Заголовок *") },
                modifier = Modifier.fillMaxWidth(),
                isError = title.value.isBlank(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                singleLine = true
            )

            if (title.value.isBlank()) {
                Text(
                    text = "Необходимо заполнить",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            OutlinedTextField(
                value = content.value,
                onValueChange = { content.value = it },
                label = { Text("Текст заметки") },
                modifier = Modifier.fillMaxWidth().height(200.dp),
                maxLines = 10,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isImportant.value,
                    onCheckedChange = { isImportant.value = it }
                )
                Spacer(Modifier.width(8.dp))
                Text("Важная заметка")
            }

            if (!isNewNote) {
                Text(
                    text = "Создано: ${initialNote.getDateString()} ${initialNote.getTimeString()}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isRead.value,
                        onCheckedChange = { isRead.value = it }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Прочитано")
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    if (title.value.isBlank()) return@Button

                    val noteToSave = (initialNote ?: Note()).copy(
                        title = title.value.trim(),
                        content = content.value.trim(),
                        isImportant = isImportant.value,
                        isRead = isRead.value
                    )

                    viewModel.saveNote(noteToSave)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isNewNote) "Добавить" else "Сохранить")
            }
        }
    }

    if (showUnsavedDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedDialog = false },
            title = { Text("Несохранённые изменения") },
            text = { Text("Закрыть без сохранения?") },
            confirmButton = {
                TextButton(onClick = { onNavigateBack() }) {
                    Text("Закрыть")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnsavedDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}
