package com.seijind.ninetolife.domain.shift.model

import kotlin.time.Duration.Companion.minutes

/** Epoch millis instead of `Instant` keeps the type plain for Swift. */
data class ActiveShift(
    val startedAtEpochMillis: Long,
    val plannedMinutes: Int,
) {
    val endsAtEpochMillis: Long get() = startedAtEpochMillis + plannedMinutes.minutes.inWholeMilliseconds
}
