package com.example.noteslist.data.local.settings

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLaunchTracker @Inject constructor(
    context: Context
) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun shouldShowInitialShimmer(): Boolean {
        return !preferences.getBoolean(KEY_INITIAL_SHIMMER_COMPLETED, false)
    }

    fun markInitialShimmerCompleted() {
        preferences.edit()
            .putBoolean(KEY_INITIAL_SHIMMER_COMPLETED, true)
            .apply()
    }

    private companion object {
        private const val PREFS_NAME = "app_launch_tracker"
        private const val KEY_INITIAL_SHIMMER_COMPLETED = "initial_shimmer_completed"
    }
}
