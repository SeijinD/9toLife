package com.seijind.ninetolife.domain.shift

import com.seijind.ninetolife.domain.shift.model.ActiveShift
import com.seijind.ninetolife.domain.shift.model.CompletedShift
import com.seijind.ninetolife.domain.shift.model.ShiftTotals
import kotlinx.coroutines.flow.Flow

interface ShiftRepository {
    val activeShift: Flow<ActiveShift?>

    val totals: Flow<ShiftTotals>

    suspend fun setActiveShift(shift: ActiveShift?)

    suspend fun saveCompleted(
        startedAtEpochMillis: Long,
        endedAtEpochMillis: Long,
        plannedMinutes: Int,
    ): CompletedShift

    suspend fun deleteCompleted(id: Long)

    fun observeStartedBetween(fromEpochMillis: Long, toEpochMillis: Long): Flow<List<CompletedShift>>
}
