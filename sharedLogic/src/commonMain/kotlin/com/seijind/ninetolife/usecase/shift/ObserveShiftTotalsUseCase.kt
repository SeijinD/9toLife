package com.seijind.ninetolife.usecase.shift

import com.seijind.ninetolife.domain.shift.ShiftRepository
import com.seijind.ninetolife.domain.shift.model.ShiftTotals
import kotlinx.coroutines.flow.Flow

class ObserveShiftTotalsUseCase(
    private val shiftRepository: ShiftRepository,
) {
    operator fun invoke(): Flow<ShiftTotals> = shiftRepository.totals
}
