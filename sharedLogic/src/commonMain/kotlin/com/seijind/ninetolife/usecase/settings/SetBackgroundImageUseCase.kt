package com.seijind.ninetolife.usecase.settings

import com.seijind.ninetolife.domain.settings.SettingsRepository

class SetBackgroundImageUseCase(
    private val settingsRepository: SettingsRepository,
) {
    /** [path] identifies the copy in the app's files (each platform resolves it); null restores the default background. */
    suspend operator fun invoke(path: String?) = settingsRepository.setBackgroundImagePath(path = path)
}
