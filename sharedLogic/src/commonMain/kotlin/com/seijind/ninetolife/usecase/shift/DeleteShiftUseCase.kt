package com.seijind.ninetolife.usecase.shift

import com.seijind.ninetolife.domain.shift.ShiftRepository
import com.seijind.ninetolife.domain.shift.model.CompletedShift

class DeleteShiftUseCase(
    private val shiftRepository: ShiftRepository,
) {
    suspend operator fun invoke(shift: CompletedShift) {
        shiftRepository.deleteCompleted(id = shift.id)
    }
}
