package com.seijind.ninetolife.ui.home.model

import com.seijind.ninetolife.domain.shift.model.ShiftTotals

data class HomeUiState(
    val isLoading: Boolean = true,
    val qrPayload: String? = null,
    val backgroundImagePath: String? = null,
    val isQrExpanded: Boolean = false,
    val shift: ShiftProgress? = null,
    val seriousMode: Boolean = false,
    val totals: ShiftTotals = ShiftTotals(),
)

data class ShiftProgress(
    val startedAtEpochMillis: Long,
    val endsAtEpochMillis: Long,
    val nowEpochMillis: Long,
) {
    val isOvertime: Boolean get() = nowEpochMillis >= endsAtEpochMillis
    val remainingMillis: Long get() = (endsAtEpochMillis - nowEpochMillis).coerceAtLeast(minimumValue = 0)
    val overtimeMillis: Long get() = (nowEpochMillis - endsAtEpochMillis).coerceAtLeast(minimumValue = 0)

    val progress: Float
        get() {
            val total = endsAtEpochMillis - startedAtEpochMillis
            if (total <= 0) return 1f
            return ((nowEpochMillis - startedAtEpochMillis).toFloat() / total).coerceIn(
                minimumValue = 0f,
                maximumValue = 1f,
            )
        }

    val mood: ShiftMood
        get() = when {
            isOvertime -> ShiftMood.OVERTIME
            progress < JustStartedUntil -> ShiftMood.JUST_STARTED
            progress < WarmingUpUntil -> ShiftMood.WARMING_UP
            progress < HalfwayUntil -> ShiftMood.HALFWAY
            progress < HomeStretchUntil -> ShiftMood.HOME_STRETCH
            else -> ShiftMood.ALMOST_FREE
        }

    private companion object {
        const val JustStartedUntil = 0.1f
        const val WarmingUpUntil = 0.4f
        const val HalfwayUntil = 0.6f
        const val HomeStretchUntil = 0.9f
    }
}

/** Which flavour of text the timer subtitle shows; each platform maps it to its own strings. */
enum class ShiftMood { JUST_STARTED, WARMING_UP, HALFWAY, HOME_STRETCH, ALMOST_FREE, OVERTIME }

sealed interface HomeEvent {
    data object OpenScanner : HomeEvent
    data object ShiftStarted : HomeEvent
    data class ShiftEnded(val workedMinutes: Long) : HomeEvent
    data object UndoDone : HomeEvent

    /** The planned time ran out while the screen was visible. */
    data object Freedom : HomeEvent
}
