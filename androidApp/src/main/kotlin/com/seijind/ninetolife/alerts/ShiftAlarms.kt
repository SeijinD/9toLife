package com.seijind.ninetolife.alerts

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.seijind.ninetolife.domain.settings.model.AppSettings
import com.seijind.ninetolife.domain.shift.model.ActiveShift
import kotlin.time.Duration.Companion.minutes

internal const val ActionTick = "com.seijind.ninetolife.action.SHIFT_TICK"
internal const val ActionReminder = "com.seijind.ninetolife.action.SHIFT_REMINDER"
internal const val ActionEnd = "com.seijind.ninetolife.action.SHIFT_END"
internal const val ActionForgotClockOut = "com.seijind.ninetolife.action.SHIFT_FORGOT_CLOCK_OUT"
internal const val ExtraStartedAt = "started_at"
internal const val ExtraPlannedMinutes = "planned_minutes"
internal const val ExtraSeriousMode = "serious_mode"
internal const val ExtraOffsetMinutes = "offset_minutes"

/** How often the progress bar is refreshed; the countdown text itself is updated by the system. */
internal val ProgressRefreshInterval = 1.minutes

/** How long after the planned end a still-running shift gets a "forgot to clock out?" nudge. */
internal val ForgotClockOutDelay = 10.minutes

private const val TickRequestCode = 1
private const val EndRequestCode = 2
private const val ForgotClockOutRequestCode = 3
private const val ReminderRequestCodeBase = 100

internal object ShiftAlarms {

    fun scheduleAll(
        context: Context,
        shift: ActiveShift,
        reminderOffsetsMinutes: List<Int>,
        seriousMode: Boolean,
        nowEpochMillis: Long,
    ) {
        cancelAll(context = context)
        reminderOffsetsMinutes.forEach { offset ->
            val at = shift.endsAtEpochMillis - offset.minutes.inWholeMilliseconds
            if (at > nowEpochMillis) {
                setPrecise(
                    context = context,
                    atEpochMillis = at,
                    intent = pendingIntent(
                        context = context,
                        action = ActionReminder,
                        requestCode = ReminderRequestCodeBase + offset,
                        shift = shift,
                        seriousMode = seriousMode,
                        offsetMinutes = offset,
                    ),
                )
            }
        }
        if (shift.endsAtEpochMillis > nowEpochMillis) {
            setPrecise(
                context = context,
                atEpochMillis = shift.endsAtEpochMillis,
                intent = pendingIntent(
                    context = context,
                    action = ActionEnd,
                    requestCode = EndRequestCode,
                    shift = shift,
                    seriousMode = seriousMode,
                ),
            )
            scheduleNextTick(context = context, shift = shift, seriousMode = seriousMode, nowEpochMillis = nowEpochMillis)
        }
        val forgotAt = shift.endsAtEpochMillis + ForgotClockOutDelay.inWholeMilliseconds
        if (forgotAt > nowEpochMillis) {
            setPrecise(
                context = context,
                atEpochMillis = forgotAt,
                intent = pendingIntent(
                    context = context,
                    action = ActionForgotClockOut,
                    requestCode = ForgotClockOutRequestCode,
                    shift = shift,
                    seriousMode = seriousMode,
                ),
            )
        }
    }

    fun scheduleNextTick(context: Context, shift: ActiveShift, seriousMode: Boolean, nowEpochMillis: Long) {
        val next = nowEpochMillis + ProgressRefreshInterval.inWholeMilliseconds
        if (next >= shift.endsAtEpochMillis) return
        // Non-wakeup and inexact: a progress bar nobody is looking at can wait for the screen to turn on.
        context.alarmManager()?.setAndAllowWhileIdle(
            AlarmManager.RTC,
            next,
            pendingIntent(
                context = context,
                action = ActionTick,
                requestCode = TickRequestCode,
                shift = shift,
                seriousMode = seriousMode,
            ),
        )
    }

    fun cancelAll(context: Context) {
        val alarmManager = context.alarmManager() ?: return
        val requests = listOf(
            ActionTick to TickRequestCode,
            ActionEnd to EndRequestCode,
            ActionForgotClockOut to ForgotClockOutRequestCode,
        ) +
            AppSettings.ReminderOffsetPresetsMinutes.map { ActionReminder to ReminderRequestCodeBase + it }
        requests.forEach { (action, requestCode) ->
            PendingIntent.getBroadcast(
                context,
                requestCode,
                Intent(context, ShiftAlarmReceiver::class.java).setAction(action),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE,
            )?.let(alarmManager::cancel)
        }
    }

    private fun setPrecise(context: Context, atEpochMillis: Long, intent: PendingIntent) {
        val alarmManager = context.alarmManager() ?: return
        val canBeExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
        if (canBeExact) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atEpochMillis, intent)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atEpochMillis, intent)
        }
    }

    private fun pendingIntent(
        context: Context,
        action: String,
        requestCode: Int,
        shift: ActiveShift,
        seriousMode: Boolean,
        offsetMinutes: Int = 0,
    ): PendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        Intent(context, ShiftAlarmReceiver::class.java)
            .setAction(action)
            .putExtra(ExtraStartedAt, shift.startedAtEpochMillis)
            .putExtra(ExtraPlannedMinutes, shift.plannedMinutes)
            .putExtra(ExtraSeriousMode, seriousMode)
            .putExtra(ExtraOffsetMinutes, offsetMinutes),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )

    private fun Context.alarmManager(): AlarmManager? = getSystemService(AlarmManager::class.java)
}
