package com.example.noteslist.domain.common

import java.util.UUID

interface IdGenerator {
    fun randomUuid(): UUID
}
