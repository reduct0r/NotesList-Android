package com.example.noteslist.domain.usecase

import com.example.noteslist.domain.model.list.ListItem
import com.example.noteslist.domain.model.list.NoteStackItem
import jakarta.inject.Inject
import java.util.UUID

class BuildNoteListUiUseCase @Inject constructor() {

    operator fun invoke(
        baseItems: List<ListItem>,
        expandedStacks: Map<List<UUID>, Boolean>,
        pendingAnimations: Set<List<UUID>>
    ): Pair<List<ListItem>, Set<List<UUID>>> {

        val consumed = mutableSetOf<List<UUID>>()

        val items = baseItems.map { item ->
            if (item is NoteStackItem) {
                val key = item.notes.mapNotNull { it.id }.sorted()

                val isExpanded = expandedStacks[key] ?: false
                val shouldAnimate = isExpanded && pendingAnimations.contains(key)

                if (shouldAnimate) consumed.add(key)

                item.copy(
                    isExpanded = isExpanded,
                    shouldAnimateExpand = shouldAnimate
                )
            } else item
        }

        return items to consumed
    }
}