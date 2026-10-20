package com.seijind.ninetolife.alerts

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.seijind.ninetolife.MainActivity
import com.seijind.ninetolife.R
import com.seijind.ninetolife.domain.shift.model.ActiveShift
import com.seijind.ninetolife.format.formatDuration
import com.seijind.ninetolife.format.formatTimeOfDay

private const val RunningChannelId = "shift_running"
private const val RemindersChannelId = "shift_reminders"
private const val RunningNotificationId = 1
private const val ReminderNotificationId = 2
private const val EndNotificationId = 3
private const val ForgotClockOutNotificationId = 4
// Per mille, so a long shift still moves the bar a little on every refresh.
private const val ProgressMax = 1000
private const val OpenAppRequestCode = 0

internal object ShiftNotifications {

    fun createChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.createNotificationChannels(
            listOf(
                NotificationChannel(
                    RunningChannelId,
                    context.getString(R.string.notification_channel_shift),
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    description = context.getString(R.string.notification_channel_shift_description)
                    setSound(null, null)
                    enableVibration(false)
                },
                NotificationChannel(
                    RemindersChannelId,
                    context.getString(R.string.notification_channel_reminders),
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = context.getString(R.string.notification_channel_reminders_description)
                },
            ),
        )
    }

    /** The live shift: a system-driven chronometer (no app work per second) plus a progress bar. */
    fun showRunning(context: Context, shift: ActiveShift, seriousMode: Boolean, nowEpochMillis: Long) {
        val isOvertime = nowEpochMillis >= shift.endsAtEpochMillis
        val elapsed = nowEpochMillis - shift.startedAtEpochMillis
        val total = (shift.endsAtEpochMillis - shift.startedAtEpochMillis).coerceAtLeast(minimumValue = 1)
        val progress = if (isOvertime) {
            ProgressMax
        } else {
            (elapsed * ProgressMax / total).toInt().coerceIn(minimumValue = 0, maximumValue = ProgressMax)
        }
        val endTime = context.formatTimeOfDay(epochMillis = shift.endsAtEpochMillis)
        val title = when {
            isOvertime && seriousMode -> context.getString(R.string.notification_overtime_title_serious)
            isOvertime -> context.getString(R.string.notification_overtime_title_fun)
            seriousMode -> context.getString(R.string.notification_running_title_serious, endTime)
            else -> context.getString(R.string.notification_running_title_fun, endTime)
        }
        val notification = NotificationCompat.Builder(context, RunningChannelId)
            .setSmallIcon(R.drawable.ic_stat_shift)
            .setContentTitle(title)
            .setContentText(
                context.getString(R.string.notification_running_text, context.formatTimeOfDay(epochMillis = shift.startedAtEpochMillis)),
            )
            .setContentIntent(context.openAppIntent())
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setShowWhen(true)
            .setWhen(shift.endsAtEpochMillis)
            .setUsesChronometer(true)
            .setChronometerCountDown(!isOvertime)
            .setProgress(ProgressMax, progress, false)
            .setStyle(
                NotificationCompat.ProgressStyle()
                    .setProgressSegments(
                        listOf(
                            NotificationCompat.ProgressStyle.Segment(ProgressMax)
                                .setColor(ContextCompat.getColor(context, R.color.brand_brass)),
                        ),
                    )
                    .setProgress(progress),
            )
            .setRequestPromotedOngoing(true)
            .build()
        context.notifyIfAllowed(id = RunningNotificationId, notification = notification)
    }

    fun showReminder(context: Context, offsetMinutes: Int, seriousMode: Boolean) {
        val left = context.formatDuration(minutes = offsetMinutes.toLong())
        val title = context.getString(
            if (seriousMode) R.string.notification_reminder_title_serious else R.string.notification_reminder_title_fun,
            left,
        )
        context.notifyIfAllowed(id = ReminderNotificationId, notification = alertBuilder(context = context, title = title).build())
    }

    fun showEnd(context: Context, seriousMode: Boolean) {
        val builder = alertBuilder(
            context = context,
            title = context.getString(if (seriousMode) R.string.notification_end_title_serious else R.string.notification_end_title_fun),
        ).setContentText(
            context.getString(if (seriousMode) R.string.notification_end_text_serious else R.string.notification_end_text_fun),
        )
        context.notifyIfAllowed(id = EndNotificationId, notification = builder.build())
    }

    fun showForgotClockOut(context: Context, seriousMode: Boolean) {
        val title = if (seriousMode) {
            context.getString(R.string.notification_forgot_title_serious)
        } else {
            context.getString(
                R.string.notification_forgot_title_fun,
                context.formatDuration(minutes = ForgotClockOutDelay.inWholeMinutes),
            )
        }
        context.notifyIfAllowed(id = ForgotClockOutNotificationId, notification = alertBuilder(context = context, title = title).build())
    }

    fun cancelAll(context: Context) {
        NotificationManagerCompat.from(context).apply {
            cancel(RunningNotificationId)
            cancel(ReminderNotificationId)
            cancel(EndNotificationId)
            cancel(ForgotClockOutNotificationId)
        }
    }

    private fun alertBuilder(context: Context, title: String): NotificationCompat.Builder =
        NotificationCompat.Builder(context, RemindersChannelId)
            .setSmallIcon(R.drawable.ic_stat_shift)
            .setContentTitle(title)
            .setContentIntent(context.openAppIntent())
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

    private fun Context.openAppIntent(): PendingIntent = PendingIntent.getActivity(
        this,
        OpenAppRequestCode,
        Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )

    /** Covers both the API 33+ runtime permission and notifications turned off in the system settings. */
    @SuppressLint("MissingPermission")
    private fun Context.notifyIfAllowed(id: Int, notification: Notification) {
        val manager = NotificationManagerCompat.from(this)
        if (!manager.areNotificationsEnabled()) return
        manager.notify(id, notification)
    }
}
