package com.example.noteslist.di

import com.example.noteslist.data.system.SystemAppClock
import com.example.noteslist.data.system.UuidIdGenerator
import com.example.noteslist.domain.common.AppClock
import com.example.noteslist.domain.common.IdGenerator
import dagger.Binds
import dagger.Module

@Module
interface PlatformModule {

    @Binds
    fun bindAppClock(
        impl: SystemAppClock
    ): AppClock

    @Binds
    fun bindIdGenerator(
        impl: UuidIdGenerator
    ): IdGenerator
}
