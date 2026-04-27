package com.example.noteslist.data.system

import com.example.noteslist.domain.common.IdGenerator
import java.util.UUID
import javax.inject.Inject

class UuidIdGenerator @Inject constructor() : IdGenerator {
    override fun randomUuid(): UUID = UUID.randomUUID()
}
