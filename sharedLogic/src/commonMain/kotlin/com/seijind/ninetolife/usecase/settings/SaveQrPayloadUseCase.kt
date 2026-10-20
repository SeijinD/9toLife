package com.seijind.ninetolife.usecase.settings

import com.seijind.ninetolife.domain.settings.SettingsRepository

class SaveQrPayloadUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(payload: String) = settingsRepository.setQrPayload(payload = payload)
}
