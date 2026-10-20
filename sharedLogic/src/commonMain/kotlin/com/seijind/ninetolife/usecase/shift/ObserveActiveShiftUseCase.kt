package com.seijind.ninetolife.usecase.shift

import com.seijind.ninetolife.domain.shift.ShiftRepository
import com.seijind.ninetolife.domain.shift.model.ActiveShift
import kotlinx.coroutines.flow.Flow

class ObserveActiveShiftUseCase(
    private val shiftRepository: ShiftRepository,
) {
    operator fun invoke(): Flow<ActiveShift?> = shiftRepository.activeShift
}
