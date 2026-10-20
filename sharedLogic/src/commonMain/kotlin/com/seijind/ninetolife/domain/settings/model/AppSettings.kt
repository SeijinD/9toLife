package com.seijind.ninetolife.domain.settings.model

data class AppSettings(
    val qrPayload: String? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val shiftDurationMinutes: Int = DefaultShiftDurationMinutes,
    val reminderOffsetsMinutes: List<Int> = DefaultReminderOffsetsMinutes,
    val hapticsEnabled: Boolean = true,
    val seriousMode: Boolean = false,
    val backgroundImagePath: String? = null,
) {
    companion object {
        const val DefaultShiftDurationMinutes: Int = 8 * 60
        const val ShiftDurationStepMinutes: Int = 15
        const val MinShiftDurationMinutes: Int = 15
        const val MaxShiftDurationMinutes: Int = 16 * 60
        val DefaultReminderOffsetsMinutes: List<Int> = listOf(30, 10)
        val ReminderOffsetPresetsMinutes: List<Int> = listOf(5, 10, 15, 30, 45, 60, 90, 120)
    }
}
