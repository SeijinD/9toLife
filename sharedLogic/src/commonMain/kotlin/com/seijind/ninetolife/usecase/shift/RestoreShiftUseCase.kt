package com.seijind.ninetolife.usecase.shift

import com.seijind.ninetolife.domain.shift.ShiftRepository
import com.seijind.ninetolife.domain.shift.model.CompletedShift

/** Puts a deleted shift back with the same times; it gets a new id. */
class RestoreShiftUseCase(
    private val shiftRepository: ShiftRepository,
) {
    suspend operator fun invoke(shift: CompletedShift) {
        shiftRepository.saveCompleted(
            startedAtEpochMillis = shift.startedAtEpochMillis,
            endedAtEpochMillis = shift.endedAtEpochMillis,
            plannedMinutes = shift.plannedMinutes,
        )
    }
}
