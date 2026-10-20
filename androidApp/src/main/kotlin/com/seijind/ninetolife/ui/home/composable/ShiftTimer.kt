package com.seijind.ninetolife.ui.home.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seijind.ninetolife.R
import com.seijind.ninetolife.format.formatClock
import com.seijind.ninetolife.ui.common.format.formatTimeOfDay
import com.seijind.ninetolife.ui.home.model.ShiftMood
import com.seijind.ninetolife.ui.home.model.ShiftProgress
import com.seijind.ninetolife.ui.theme.TabularFigures

private val ProgressWidth = 240.dp
private val ProgressHeight = 8.dp
private val Spacing = 8.dp

@Composable
internal fun ShiftTimer(
    shift: ShiftProgress,
    seriousMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = Spacing),
    ) {
        if (shift.isOvertime) {
            Text(
                text = stringResource(id = R.string.home_timer_overtime_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Text(
            text = if (shift.isOvertime) {
                stringResource(id = R.string.home_timer_overtime_value, formatClock(millis = shift.overtimeMillis))
            } else {
                formatClock(millis = shift.remainingMillis)
            },
            style = MaterialTheme.typography.displayMedium.copy(fontFeatureSettings = TabularFigures),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        ShiftProgressBar(progress = shift.progress)
        AnimatedContent(
            targetState = shift.mood,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "shiftMood",
        ) { mood ->
            Text(
                text = stringResource(id = mood.textRes(seriousMode = seriousMode)),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
        }
        Text(
            text = stringResource(id = R.string.home_timer_ends_at, formatTimeOfDay(epochMillis = shift.endsAtEpochMillis)),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** One rounded bar that fills up; M3's indicator draws the filled part and the track as two separate pills. */
@Composable
private fun ShiftProgressBar(progress: Float) {
    Box(
        modifier = Modifier
            .width(width = ProgressWidth)
            .height(height = ProgressHeight)
            .clip(shape = CircleShape)
            .background(color = ProgressIndicatorDefaults.linearTrackColor)
            .progressSemantics(value = progress),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = progress)
                .background(
                    color = ProgressIndicatorDefaults.linearColor,
                    shape = CircleShape,
                ),
        )
    }
}

private fun ShiftMood.textRes(seriousMode: Boolean): Int = when {
    seriousMode && this == ShiftMood.OVERTIME -> R.string.home_mood_overtime_serious
    seriousMode -> R.string.home_mood_serious
    else -> when (this) {
        ShiftMood.JUST_STARTED -> R.string.home_mood_just_started
        ShiftMood.WARMING_UP -> R.string.home_mood_warming_up
        ShiftMood.HALFWAY -> R.string.home_mood_halfway
        ShiftMood.HOME_STRETCH -> R.string.home_mood_home_stretch
        ShiftMood.ALMOST_FREE -> R.string.home_mood_almost_free
        ShiftMood.OVERTIME -> R.string.home_mood_overtime
    }
}
