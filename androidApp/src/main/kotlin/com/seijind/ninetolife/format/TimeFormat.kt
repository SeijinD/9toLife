package com.seijind.ninetolife.format

import android.content.Context
import android.text.format.DateFormat
import com.seijind.ninetolife.R
import java.util.Date
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

/** "07:59:59" for a running countdown. */
internal fun formatClock(millis: Long): String =
    millis.milliseconds.toComponents { hours, minutes, seconds, _ ->
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    }

/** Short wall-clock time in the user's 12/24h preference. */
internal fun Context.formatTimeOfDay(epochMillis: Long): String =
    DateFormat.getTimeFormat(this).format(Date(epochMillis))

internal fun Context.formatDuration(minutes: Long): String =
    minutes.minutes.toComponents { hours, mins, _, _ ->
        if (hours == 0L) {
            getString(R.string.common_duration_minutes, mins)
        } else {
            getString(R.string.common_duration_hours_minutes, hours.toInt(), mins)
        }
    }
