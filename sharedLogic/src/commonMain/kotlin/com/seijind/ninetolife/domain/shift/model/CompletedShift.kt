package com.seijind.ninetolife.domain.shift.model

import kotlin.time.Duration.Companion.milliseconds

data class CompletedShift(
    val id: Long,
    val startedAtEpochMillis: Long,
    val endedAtEpochMillis: Long,
    val plannedMinutes: Int,
) {
    val workedMinutes: Long get() = (endedAtEpochMillis - startedAtEpochMillis).milliseconds.inWholeMinutes
    val overtimeMinutes: Long get() = (workedMinutes - plannedMinutes).coerceAtLeast(minimumValue = 0)
    val earlyLeaveMinutes: Long get() = (plannedMinutes - workedMinutes).coerceAtLeast(minimumValue = 0)
}
