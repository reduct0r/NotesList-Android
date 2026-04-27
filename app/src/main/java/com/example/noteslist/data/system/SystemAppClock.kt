package com.example.noteslist.data.system

import android.os.SystemClock
import com.example.noteslist.domain.common.AppClock
import javax.inject.Inject

class SystemAppClock @Inject constructor() : AppClock {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()

    override fun elapsedRealtime(): Long = SystemClock.elapsedRealtime()
}
