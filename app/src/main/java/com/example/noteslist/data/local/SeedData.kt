package com.example.noteslist.data.local

import com.example.noteslist.data.local.entity.NoteEntity
import com.example.noteslist.domain.common.AppClock
import java.nio.charset.StandardCharsets
import java.util.UUID
import javax.inject.Inject

class SeedData @Inject constructor(
    private val appClock: AppClock
) {

    private companion object {
        private const val HOUR = 1000 * 60 * 60L
        private const val DAY = HOUR * 24
    }

    private fun seedId(suffix: String): String {
        return UUID.nameUUIDFromBytes("seed-note-$suffix".toByteArray(StandardCharsets.UTF_8)).toString()
    }

    fun notes(): List<NoteEntity> {
        val now = appClock.currentTimeMillis()

        return listOf(
            NoteEntity(seedId("1"), "Р”Р— РїРѕ Android", "RecyclerView & Delegates", now - HOUR, true, false),
            NoteEntity(seedId("2"), "Р”Р— РїРѕ РђРёРЎР”", "РђР»РіРѕСЂРёС‚РјС‹ РЅР° РіСЂР°С„Р°С…", now - HOUR * 2, true, false),
            NoteEntity(seedId("3"), "РљРѕСЂРј РєРѕС‚Сѓ", "РљСѓРїРёС‚СЊ РІР»Р°Р¶РЅС‹Р№", now - HOUR * 3, false, false),
            NoteEntity(seedId("4"), "Р—Р°Р»", "РўСЂРµРЅРёСЂРѕРІРєР° РІ 19:00", now - HOUR * 4, false, false),
            NoteEntity(seedId("5"), "РЈР¶РёРЅ", "РџСЂРёРіРѕС‚РѕРІРёС‚СЊ РїР°СЃС‚Сѓ", now - HOUR * 5, false, false),
            NoteEntity(seedId("6"), "РџРѕР·РІРѕРЅРёС‚СЊ РјР°РјРµ", "РџРѕР·РґСЂР°РІРёС‚СЊ!", now - DAY - HOUR, true, false),
            NoteEntity(seedId("7"), "РЎСЂРѕС‡РЅС‹Р№ Р±Р°Рі", "РџРѕС„РёРєСЃРёС‚СЊ РІ РїСЂРѕРґРµ", now - DAY - HOUR * 2, true, false),
            NoteEntity(seedId("8"), "РљСѓРїРёС‚СЊ РјРѕР»РѕРєРѕ", "2.5%", now - DAY * 2 - HOUR, false, false),
            NoteEntity(seedId("9"), "РџРѕРјС‹С‚СЊ РјР°С€РёРЅСѓ", "РЎР°РјРѕРѕР±СЃР»СѓР¶РёРІР°РЅРёРµ", now - DAY * 2 - HOUR * 3, false, false),
            NoteEntity(seedId("10"), "Р—Р°РјРµС‚РєР° 1", "РўРµРєСЃС‚", now - DAY * 3 - HOUR, false, false),
            NoteEntity(seedId("11"), "Р—Р°РјРµС‚РєР° 2", "РўРµРєСЃС‚", now - DAY * 3 - HOUR * 2, false, false),
            NoteEntity(seedId("12"), "Р—Р°РјРµС‚РєР° 3", "РўРµРєСЃС‚", now - DAY * 3 - HOUR * 3, false, false),
            NoteEntity(seedId("13"), "Р—Р°РјРµС‚РєР° 4", "РўРµРєСЃС‚", now - DAY * 3 - HOUR * 4, false, false),
            NoteEntity(seedId("14"), "Р—Р°РјРµС‚РєР° 5", "РўРµРєСЃС‚", now - DAY * 3 - HOUR * 5, false, false),
            NoteEntity(seedId("15"), "РћРґРёРЅРѕРєР°СЏ Р·Р°РјРµС‚РєР°", "РЇ РѕРґРЅР° РІ СЌС‚РѕРј РґРЅРµ", now - DAY * 4, false, false),
            NoteEntity(seedId("16"), "РЎС‚Р°СЂР°СЏ РІР°Р¶РЅР°СЏ", "РџСЂРѕРІРµСЂРєР° Р°СЂС…РёРІР°", now - DAY * 7, true, false),
            NoteEntity(seedId("17"), "РЎС‚Р°СЂР°СЏ РѕР±С‹С‡РЅР°СЏ", "РџСЂРѕРІРµСЂРєР° Р°СЂС…РёРІР°", now - DAY * 7 - HOUR, false, false),
            NoteEntity(seedId("18"), "РџСЂРѕС€Р»С‹Р№ РіРѕРґ", "РЎ РќРѕРІС‹Рј Р“РѕРґРѕРј!", now - DAY * 400, false, false)
        )
    }
}
