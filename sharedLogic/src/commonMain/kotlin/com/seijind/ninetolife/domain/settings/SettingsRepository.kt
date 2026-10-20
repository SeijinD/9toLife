package com.seijind.ninetolife.domain.settings

import com.seijind.ninetolife.domain.settings.model.AppLanguage
import com.seijind.ninetolife.domain.settings.model.AppSettings
import com.seijind.ninetolife.domain.settings.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>

    suspend fun setQrPayload(payload: String?)

    suspend fun setThemeMode(mode: ThemeMode)

    suspend fun setLanguage(language: AppLanguage)

    /** Reads and writes in one storage transaction, so quick repeated taps never read a stale value. */
    suspend fun updateShiftDurationMinutes(transform: (Int) -> Int)

    /** Reads and writes in one storage transaction, so quick repeated taps never read a stale value. */
    suspend fun updateReminderOffsetsMinutes(transform: (List<Int>) -> List<Int>)

    suspend fun setHapticsEnabled(enabled: Boolean)

    suspend fun setSeriousMode(enabled: Boolean)

    suspend fun setBackgroundImagePath(path: String?)
}
