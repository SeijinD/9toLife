package com.seijind.ninetolife.alerts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.seijind.ninetolife.domain.shift.model.ActiveShift

/** Receives the shift alarms. The shift travels in the extras, so nothing is read from storage here. */
internal class ShiftAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val startedAt = intent.getLongExtra(ExtraStartedAt, 0)
        val plannedMinutes = intent.getIntExtra(ExtraPlannedMinutes, 0)
        if (startedAt == 0L || plannedMinutes == 0) return
        val shift = ActiveShift(startedAtEpochMillis = startedAt, plannedMinutes = plannedMinutes)
        val seriousMode = intent.getBooleanExtra(ExtraSeriousMode, false)
        val now = System.currentTimeMillis()

        when (intent.action) {
            ActionTick -> {
                ShiftNotifications.showRunning(context = context, shift = shift, seriousMode = seriousMode, nowEpochMillis = now)
                ShiftAlarms.scheduleNextTick(context = context, shift = shift, seriousMode = seriousMode, nowEpochMillis = now)
            }
            ActionReminder -> ShiftNotifications.showReminder(
                context = context,
                offsetMinutes = intent.getIntExtra(ExtraOffsetMinutes, 0),
                seriousMode = seriousMode,
            )
            ActionEnd -> {
                ShiftNotifications.showEnd(context = context, seriousMode = seriousMode)
                // Switches the live notification to the overtime count-up.
                ShiftNotifications.showRunning(context = context, shift = shift, seriousMode = seriousMode, nowEpochMillis = now)
            }
            ActionForgotClockOut -> ShiftNotifications.showForgotClockOut(context = context, seriousMode = seriousMode)
        }
    }
}
