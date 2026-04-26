package com.example.noteslist.domain.usecase

import jakarta.inject.Inject
import java.util.UUID

class ToggleStackUseCase @Inject constructor() {

    data class Params(
        val key: List<UUID>,
        val expanded: MutableMap<List<UUID>, Boolean>,
        val animations: MutableSet<List<UUID>>,
    )

    operator fun invoke(params: Params) {
        val (key, expanded, animations) = params

        val currentlyExpanded = expanded[key] == true
        val newState = !currentlyExpanded

        if (newState) {
            expanded[key] = true
            animations.add(key)
        } else {
            expanded.remove(key)
            animations.remove(key)
        }
    }
}