package com.example.noteslist.data.local.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.noteslist.presentation.notesList.StackSettings
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class StackSettingsStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun getStackSettings(): StackSettings? {
        return dataStore.data
            .map { preferences ->
                val stackSpacing = preferences[STACK_SPACING_KEY]
                val stackMaxVisible = preferences[STACK_MAX_VISIBLE_KEY]

                if (stackSpacing == null || stackMaxVisible == null) {
                    null
                } else {
                    StackSettings(
                        stackSpacing = stackSpacing,
                        stackMaxVisible = stackMaxVisible
                    )
                }
            }
            .first()
    }

    suspend fun saveStackSettings(settings: StackSettings) {
        dataStore.edit { preferences ->
            preferences[STACK_SPACING_KEY] = settings.stackSpacing
            preferences[STACK_MAX_VISIBLE_KEY] = settings.stackMaxVisible
        }
    }

    private companion object {
        val STACK_SPACING_KEY = intPreferencesKey("stack_spacing")
        val STACK_MAX_VISIBLE_KEY = intPreferencesKey("stack_max_visible")
    }
}
