package com.seijind.ninetolife.usecase.shift

import com.seijind.ninetolife.domain.shift.ShiftAlerts
import com.seijind.ninetolife.domain.shift.ShiftRepository
import com.seijind.ninetolife.domain.shift.model.CompletedShift
import kotlinx.coroutines.flow.first
import kotlin.time.Clock

class EndShiftUseCase(
    private val shiftRepository: ShiftRepository,
    private val shiftAlerts: ShiftAlerts,
    private val clock: Clock,
) {
    /** Returns null when no shift was running. */
    suspend operator fun invoke(): CompletedShift? {
        val active = shiftRepository.activeShift.first() ?: return null
        val completed = shiftRepository.saveCompleted(
            startedAtEpochMillis = active.startedAtEpochMillis,
            endedAtEpochMillis = clock.now().toEpochMilliseconds(),
            plannedMinutes = active.plannedMinutes,
        )
        shiftRepository.setActiveShift(shift = null)
        shiftAlerts.cancel()
        return completed
    }
}
