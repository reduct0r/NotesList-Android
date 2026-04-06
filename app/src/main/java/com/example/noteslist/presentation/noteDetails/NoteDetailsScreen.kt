package com.example.noteslist.presentation.noteDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.noteslist.domain.model.Note

@Composable
fun NoteDetailsScreen(
    note: Note? = null
) {
    val isEditMode = note != null

    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    var isImportant by remember { mutableStateOf(note?.isImportant ?: false) }
    var isRead by remember { mutableStateOf(note?.isRead ?: false) }

    val isTitleError = title.isBlank()

    Column(
        Modifier.height(10.dp),
    ) {
        Text("Test")
    }
}

@Preview(showBackground = true)
@Composable
fun NoteDetailsScreenPreview() {
    NoteDetailsScreen(
        note = Note(
            id = 1,
            title = "Пример заметки",
            content = "Текст заметки",
            isImportant = true,
            isRead = false
        )
    )
}