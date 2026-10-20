package com.seijind.ninetolife.usecase.shift

import com.seijind.ninetolife.domain.shift.ShiftRepository
import com.seijind.ninetolife.domain.shift.model.CompletedShift
import kotlinx.coroutines.flow.Flow

class ObserveShiftsStartedBetweenUseCase(
    private val shiftRepository: ShiftRepository,
) {
    operator fun invoke(fromEpochMillis: Long, toEpochMillis: Long): Flow<List<CompletedShift>> =
        shiftRepository.observeStartedBetween(fromEpochMillis = fromEpochMillis, toEpochMillis = toEpochMillis)
}
