package com.seijind.ninetolife.domain.shift

import com.seijind.ninetolife.domain.shift.model.ActiveShift

/** Platform side of a running shift: the live notification / Live Activity and the reminders. */
interface ShiftAlerts {
    fun schedule(shift: ActiveShift, reminderOffsetsMinutes: List<Int>, seriousMode: Boolean)

    fun cancel()
}
