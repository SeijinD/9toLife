package com.seijind.ninetolife.ui.common.locale

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.seijind.ninetolife.domain.settings.model.AppLanguage

/**
 * On Android the per-app language belongs to the system (it can also be changed from the
 * system settings), so it is read and written here instead of the shared settings.
 */
internal fun currentAppLanguage(): AppLanguage {
    val tag = AppCompatDelegate.getApplicationLocales()[0]?.language
    return AppLanguage.entries.firstOrNull { it.tag != null && it.tag == tag } ?: AppLanguage.SYSTEM
}

internal fun applyAppLanguage(language: AppLanguage) {
    val locales = language.tag?.let { LocaleListCompat.forLanguageTags(it) } ?: LocaleListCompat.getEmptyLocaleList()
    AppCompatDelegate.setApplicationLocales(locales)
}
