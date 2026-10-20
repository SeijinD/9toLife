package com.seijind.ninetolife.usecase.shift

import com.seijind.ninetolife.domain.shift.ShiftAlerts
import com.seijind.ninetolife.domain.shift.ShiftRepository

/** Undo of a shift start: the shift never happened, nothing goes to history. */
class CancelShiftStartUseCase(
    private val shiftRepository: ShiftRepository,
    private val shiftAlerts: ShiftAlerts,
) {
    suspend operator fun invoke() {
        shiftRepository.setActiveShift(shift = null)
        shiftAlerts.cancel()
    }
}
