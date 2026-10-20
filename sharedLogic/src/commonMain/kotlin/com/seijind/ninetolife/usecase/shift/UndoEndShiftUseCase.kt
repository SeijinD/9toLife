package com.seijind.ninetolife.usecase.shift

import com.seijind.ninetolife.domain.shift.ShiftRepository
import com.seijind.ninetolife.domain.shift.model.ActiveShift
import com.seijind.ninetolife.domain.shift.model.CompletedShift

class UndoEndShiftUseCase(
    private val shiftRepository: ShiftRepository,
    private val restoreShiftAlerts: RestoreShiftAlertsUseCase,
) {
    suspend operator fun invoke(completed: CompletedShift) {
        shiftRepository.deleteCompleted(id = completed.id)
        shiftRepository.setActiveShift(
            shift = ActiveShift(
                startedAtEpochMillis = completed.startedAtEpochMillis,
                plannedMinutes = completed.plannedMinutes,
            ),
        )
        restoreShiftAlerts()
    }
}
