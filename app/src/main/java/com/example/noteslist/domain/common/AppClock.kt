package com.example.noteslist.domain.common

interface AppClock {
    fun currentTimeMillis(): Long
    fun elapsedRealtime(): Long
}
