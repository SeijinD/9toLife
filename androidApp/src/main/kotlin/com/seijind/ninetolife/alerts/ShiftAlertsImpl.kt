package com.seijind.ninetolife.alerts

import android.content.Context
import com.seijind.ninetolife.domain.shift.ShiftAlerts
import com.seijind.ninetolife.domain.shift.model.ActiveShift

internal class ShiftAlertsImpl(
    private val context: Context,
) : ShiftAlerts {

    override fun schedule(shift: ActiveShift, reminderOffsetsMinutes: List<Int>, seriousMode: Boolean) {
        val now = System.currentTimeMillis()
        ShiftNotifications.showRunning(context = context, shift = shift, seriousMode = seriousMode, nowEpochMillis = now)
        ShiftAlarms.scheduleAll(
            context = context,
            shift = shift,
            reminderOffsetsMinutes = reminderOffsetsMinutes,
            seriousMode = seriousMode,
            nowEpochMillis = now,
        )
    }

    override fun cancel() {
        ShiftAlarms.cancelAll(context = context)
        ShiftNotifications.cancelAll(context = context)
    }
}
