package com.seijind.ninetolife.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.seijind.ninetolife.domain.settings.SettingsRepository
import com.seijind.ninetolife.domain.settings.model.AppLanguage
import com.seijind.ninetolife.domain.settings.model.AppSettings
import com.seijind.ninetolife.domain.settings.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private const val OffsetSeparator = ","

internal class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    override val settings: Flow<AppSettings> = dataStore.data
        .map { preferences -> preferences.toAppSettings() }
        .distinctUntilChanged()

    override suspend fun setQrPayload(payload: String?) = edit(key = Keys.QrPayload, value = payload)

    override suspend fun setThemeMode(mode: ThemeMode) = edit(key = Keys.ThemeMode, value = mode.name)

    override suspend fun setLanguage(language: AppLanguage) = edit(key = Keys.Language, value = language.name)

    override suspend fun updateShiftDurationMinutes(transform: (Int) -> Int) {
        dataStore.edit { preferences ->
            preferences[Keys.ShiftDurationMinutes] = transform(preferences.toAppSettings().shiftDurationMinutes)
        }
    }

    override suspend fun updateReminderOffsetsMinutes(transform: (List<Int>) -> List<Int>) {
        dataStore.edit { preferences ->
            val offsets = transform(preferences.toAppSettings().reminderOffsetsMinutes)
            preferences[Keys.ReminderOffsets] = offsets.joinToString(separator = OffsetSeparator)
        }
    }

    override suspend fun setHapticsEnabled(enabled: Boolean) = edit(key = Keys.HapticsEnabled, value = enabled)

    override suspend fun setSeriousMode(enabled: Boolean) = edit(key = Keys.SeriousMode, value = enabled)

    override suspend fun setBackgroundImagePath(path: String?) = edit(key = Keys.BackgroundImagePath, value = path)

    private suspend fun <T> edit(key: Preferences.Key<T>, value: T?) {
        dataStore.edit { preferences ->
            if (value == null) preferences.remove(key = key) else preferences[key] = value
        }
    }

    private fun Preferences.toAppSettings(): AppSettings {
        val defaults = AppSettings()
        return AppSettings(
            qrPayload = this[Keys.QrPayload],
            themeMode = this[Keys.ThemeMode]
                ?.let { name -> ThemeMode.entries.firstOrNull { it.name == name } }
                ?: defaults.themeMode,
            language = this[Keys.Language]
                ?.let { name -> AppLanguage.entries.firstOrNull { it.name == name } }
                ?: defaults.language,
            shiftDurationMinutes = this[Keys.ShiftDurationMinutes] ?: defaults.shiftDurationMinutes,
            reminderOffsetsMinutes = this[Keys.ReminderOffsets]
                ?.split(OffsetSeparator)
                ?.mapNotNull { it.toIntOrNull() }
                ?: defaults.reminderOffsetsMinutes,
            hapticsEnabled = this[Keys.HapticsEnabled] ?: defaults.hapticsEnabled,
            seriousMode = this[Keys.SeriousMode] ?: defaults.seriousMode,
            backgroundImagePath = this[Keys.BackgroundImagePath],
        )
    }

    private object Keys {
        val QrPayload = stringPreferencesKey(name = "qr_payload")
        val ThemeMode = stringPreferencesKey(name = "theme_mode")
        val Language = stringPreferencesKey(name = "language")
        val ShiftDurationMinutes = intPreferencesKey(name = "shift_duration_minutes")
        val ReminderOffsets = stringPreferencesKey(name = "reminder_offsets_minutes")
        val HapticsEnabled = booleanPreferencesKey(name = "haptics_enabled")
        val SeriousMode = booleanPreferencesKey(name = "serious_mode")
        val BackgroundImagePath = stringPreferencesKey(name = "background_image_path")
    }
}
