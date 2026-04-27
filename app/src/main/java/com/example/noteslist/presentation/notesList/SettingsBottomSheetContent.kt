package com.example.noteslist.presentation.notesList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun SettingsBottomSheetContent(
    stackSettings: StackSettings,
    onApply: (Int, Int) -> Unit
) {
    var stackSpacingText by rememberSaveable { mutableStateOf("") }
    var stackMaxVisibleText by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(stackSettings) {
        stackSpacingText = stackSettings.stackSpacing.toString()
        stackMaxVisibleText = stackSettings.stackMaxVisible.toString()
    }

    val stackSpacing = stackSpacingText.toIntOrNull()
    val stackMaxVisible = stackMaxVisibleText.toIntOrNull()

    val isSpacingValid =
        stackSpacingText.isBlank() || (stackSpacing != null && stackSpacing >= 0)

    val isMaxVisibleValid =
        stackMaxVisibleText.isBlank() || (stackMaxVisible != null && stackMaxVisible >= 1)

    val isApplyEnabled = isSpacingValid && isMaxVisibleValid

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .imePadding()
    ) {
        Text("Settings")

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = stackSpacingText,
            onValueChange = { stackSpacingText = it.filter(Char::isDigit) },
            label = { Text("stackSpacing") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = stackMaxVisibleText,
            onValueChange = { stackMaxVisibleText = it.filter(Char::isDigit) },
            label = { Text("stackMaxVisible") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                onApply(
                    stackSpacing ?: stackSettings.stackSpacing,
                    stackMaxVisible ?: stackSettings.stackMaxVisible
                )
            },
            enabled = isApplyEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Apply")
        }
    }
}
