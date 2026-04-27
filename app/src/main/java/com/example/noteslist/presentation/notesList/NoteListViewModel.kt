package com.example.noteslist.presentation.notesList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.data.local.settings.AppLaunchTracker
import com.example.noteslist.data.local.settings.StackSettingsStore
import com.example.noteslist.di.ApplicationScope
import com.example.noteslist.domain.common.AppClock
import com.example.noteslist.domain.repository.NoteRepository
import com.example.noteslist.domain.model.list.ListItem
import com.example.noteslist.domain.model.list.NoteStackItem
import com.example.noteslist.domain.usecase.BuildNoteListUiUseCase
import com.example.noteslist.domain.usecase.PrepareNoteListUseCase
import com.example.noteslist.domain.usecase.ToggleNoteReadStatusUseCase
import com.example.noteslist.domain.usecase.ToggleStackUseCase
import jakarta.inject.Inject
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

class NoteListViewModel @Inject constructor(
    @param:ApplicationScope private val applicationScope: CoroutineScope,
    private val appLaunchTracker: AppLaunchTracker,
    private val stackSettingsStore: StackSettingsStore,
    private val appClock: AppClock,
    private val repository: NoteRepository,
    private val prepareUseCase: PrepareNoteListUseCase,
    private val buildNoteListUiUseCase: BuildNoteListUiUseCase,
    private val toggleStackUseCase: ToggleStackUseCase,
    private val toggleNoteReadStatusUseCase: ToggleNoteReadStatusUseCase
) : ViewModel() {

    private val expandedStacks = MutableStateFlow<Map<List<UUID>, Boolean>>(emptyMap())
    private val pendingExpandAnimations = mutableSetOf<List<UUID>>()
    private var isStackSettingsInitialized = false
    private var loadPersistedSettingsJob: Job? = null
    private var searchDebounceJob: Job? = null

    private val _stackSettings = MutableStateFlow(StackSettings())
    val stackSettings: StateFlow<StackSettings> = _stackSettings

    private val _showInitialShimmer = MutableStateFlow(appLaunchTracker.shouldShowInitialShimmer())
    val showInitialShimmer: StateFlow<Boolean> = _showInitialShimmer

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val appliedSearchQuery = MutableStateFlow("")

    init {
        observeInitialLoad()
        observeSearchQuery()
    }

    val uiItems: StateFlow<List<ListItem>> = combine(
        repository.notes,
        expandedStacks,
        appliedSearchQuery
    ) { notes, expanded, query ->
        val visibleNotes = if (query.isBlank()) {
            notes
        } else {
            notes.filter { note ->
                note.title.contains(query, ignoreCase = true)
            }
        }

        val baseItems = prepareUseCase(visibleNotes)
        val (items, consumedAnimations) = buildNoteListUiUseCase(
            baseItems = baseItems,
            expandedStacks = expanded,
            pendingAnimations = pendingExpandAnimations
        )

        pendingExpandAnimations.removeAll(consumedAnimations)

        items
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(value: String) {
        _searchQuery.value = value
    }

    fun toggleNoteReadStatus(noteId: UUID?) {
        val targetId = noteId ?: return
        applicationScope.launch {
            runCatching {
                repository.notes.first().firstOrNull { it.id == targetId }?.let { note ->
                    toggleNoteReadStatusUseCase(note)
                }
            }
        }
    }

    fun toggleStack(target: NoteStackItem) {
        val key = target.notes.mapNotNull { it.id }.sorted()
        expandedStacks.update { current ->
            current.toMutableMap().apply {
                toggleStackUseCase(
                    ToggleStackUseCase.Params(
                        key = key,
                        expanded = this,
                        animations = pendingExpandAnimations
                    )
                )
            }
        }
    }

    fun initializeStackSettingsIfNeeded(settings: StackSettings) {
        if (isStackSettingsInitialized) return
        isStackSettingsInitialized = true
        _stackSettings.value = settings

        loadPersistedSettingsJob = viewModelScope.launch {
            stackSettingsStore.getStackSettings()?.let { persistedSettings ->
                _stackSettings.value = persistedSettings
            }
        }
    }

    fun updateStackSettings(stackSpacing: Int, maxVisible: Int) {
        loadPersistedSettingsJob?.cancel()
        loadPersistedSettingsJob = null

        val updatedSettings = _stackSettings.updateAndGet {
            it.copy(
                stackSpacing = stackSpacing.coerceAtLeast(0),
                stackMaxVisible = maxVisible.coerceAtLeast(1)
            )
        }

        applicationScope.launch {
            runCatching {
                stackSettingsStore.saveStackSettings(updatedSettings)
            }
        }
    }

    private fun observeInitialLoad() {
        viewModelScope.launch {
            if (!_showInitialShimmer.value) return@launch

            val shimmerStartedAt = appClock.elapsedRealtime()

            repository.notes.first { it.isNotEmpty() }

            val elapsed = appClock.elapsedRealtime() - shimmerStartedAt
            val remainingDuration = (MIN_SHIMMER_DURATION_MS - elapsed).coerceAtLeast(0L)
            if (remainingDuration > 0) {
                delay(remainingDuration)
            }

            _showInitialShimmer.value = false
            appLaunchTracker.markInitialShimmerCompleted()
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery.collect { rawQuery ->
                searchDebounceJob?.cancel()

                val normalizedQuery = rawQuery.trim()
                if (normalizedQuery.isEmpty()) {
                    if (appliedSearchQuery.value.isNotEmpty()) {
                        appliedSearchQuery.value = ""
                    }
                    return@collect
                }

                searchDebounceJob = launch {
                    delay(SEARCH_DEBOUNCE_MS)
                    if (appliedSearchQuery.value != normalizedQuery) {
                        appliedSearchQuery.value = normalizedQuery
                    }
                }
            }
        }
    }

    private companion object {
        private const val MIN_SHIMMER_DURATION_MS = 500L
        private const val SEARCH_DEBOUNCE_MS = 300L
    }
}
