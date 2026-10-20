package com.seijind.ninetolife.usecase.settings

import com.seijind.ninetolife.domain.settings.SettingsRepository

class DeleteQrPayloadUseCase(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke() = settingsRepository.setQrPayload(payload = null)
}
