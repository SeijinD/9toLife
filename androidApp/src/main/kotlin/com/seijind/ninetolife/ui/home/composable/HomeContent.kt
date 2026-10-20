package com.seijind.ninetolife.ui.home.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seijind.ninetolife.R
import com.seijind.ninetolife.domain.shift.model.ShiftTotals
import com.seijind.ninetolife.ui.common.format.formatDuration
import com.seijind.ninetolife.ui.home.model.HomeActions
import com.seijind.ninetolife.ui.home.model.HomeUiState
import com.seijind.ninetolife.ui.home.model.ShiftProgress
import com.seijind.ninetolife.ui.theme.AppTheme
import com.seijind.ninetolife.ui.theme.QrBackground
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private val TopBarPadding = 8.dp
private val QrCardShape = RoundedCornerShape(size = 28.dp)
private val QrCardSize = 248.dp
private val QrCardPadding = 20.dp
private val QrCardElevation = 6.dp
private val SectionSpacing = 32.dp
private val TotalsPadding = 24.dp
private const val QrEnterScale = 0.92f
private const val ExpandedQrStartScale = 0.9f

@Composable
internal fun HomeContent(
    uiState: HomeUiState,
    backgroundImage: ImageBitmap?,
    actions: HomeActions,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        HomeBackground(image = backgroundImage)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
        ) {
            TopBar(onHistoryClick = actions.onHistoryClick, onSettingsClick = actions.onSettingsClick)
            Box(
                modifier = Modifier
                    .weight(weight = 1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                if (!uiState.isLoading) {
                    AnimatedContent(
                        targetState = uiState.qrPayload,
                        transitionSpec = { (fadeIn() + scaleIn(initialScale = QrEnterScale)) togetherWith fadeOut() },
                        label = "homeQr",
                    ) { payload ->
                        if (payload == null) {
                            AddQrPrompt(seriousMode = uiState.seriousMode, onAddQrClick = actions.onAddQrClick)
                        } else {
                            QrSection(
                                payload = payload,
                                shift = uiState.shift,
                                seriousMode = uiState.seriousMode,
                                onQrClick = actions.onQrClick,
                            )
                        }
                    }
                }
            }
            if (uiState.totals.shiftCount > 0) {
                TotalsLine(totals = uiState.totals, seriousMode = uiState.seriousMode)
            }
        }
        AnimatedVisibility(
            visible = uiState.isQrExpanded && uiState.qrPayload != null,
            enter = fadeIn() + scaleIn(initialScale = ExpandedQrStartScale),
            exit = fadeOut() + scaleOut(targetScale = ExpandedQrStartScale),
        ) {
            uiState.qrPayload?.let { payload -> ExpandedQr(payload = payload, onDismiss = actions.onQrDismiss) }
        }
    }
}

@Composable
private fun TopBar(
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = TopBarPadding),
    ) {
        IconButton(onClick = onHistoryClick) {
            Icon(imageVector = Icons.Filled.DateRange, contentDescription = stringResource(id = R.string.home_history_cd))
        }
        Spacer(modifier = Modifier.weight(weight = 1f))
        IconButton(onClick = onSettingsClick) {
            Icon(imageVector = Icons.Filled.Settings, contentDescription = stringResource(id = R.string.home_settings_cd))
        }
    }
}

@Composable
private fun QrSection(
    payload: String,
    shift: ShiftProgress?,
    seriousMode: Boolean,
    onQrClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = SectionSpacing),
    ) {
        Card(
            onClick = onQrClick,
            shape = QrCardShape,
            colors = CardDefaults.cardColors(containerColor = QrBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = QrCardElevation),
        ) {
            QrImage(
                payload = payload,
                contentDescription = stringResource(id = R.string.home_qr_cd),
                modifier = Modifier
                    .padding(all = QrCardPadding)
                    .size(size = QrCardSize),
            )
        }
        if (shift != null) {
            ShiftTimer(shift = shift, seriousMode = seriousMode)
        } else {
            Text(
                text = stringResource(id = if (seriousMode) R.string.home_idle_hint_serious else R.string.home_idle_hint_fun),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = TotalsPadding),
            )
        }
    }
}

@Composable
private fun TotalsLine(
    totals: ShiftTotals,
    seriousMode: Boolean,
) {
    val count = pluralStringResource(
        id = if (seriousMode) R.plurals.home_totals_shifts else R.plurals.home_totals_galleys,
        count = totals.shiftCount,
        totals.shiftCount,
    )
    val text = if (totals.overtimeMinutes > 0) {
        val overtime = stringResource(
            id = if (seriousMode) R.string.home_totals_overtime_serious else R.string.home_totals_overtime_fun,
            formatDuration(minutes = totals.overtimeMinutes),
        )
        stringResource(id = R.string.home_totals_line, count, overtime)
    } else {
        count
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = TotalsPadding),
    )
}

@Composable
@Preview
@Preview(locale = "el", fontScale = 1.5f)
private fun HomeContentPreview() {
    val now = 1_760_700_000_000L
    AppTheme(darkTheme = false) {
        HomeContent(
            uiState = HomeUiState(
                isLoading = false,
                qrPayload = "https://example.gov.gr/card/4F7A1C",
                shift = ShiftProgress(
                    startedAtEpochMillis = now - 3.hours.inWholeMilliseconds,
                    endsAtEpochMillis = now + 5.hours.inWholeMilliseconds,
                    nowEpochMillis = now,
                ),
                totals = ShiftTotals(shiftCount = 37, overtimeMinutes = 12.hours.inWholeMinutes + 30.minutes.inWholeMinutes),
            ),
            backgroundImage = null,
            actions = previewActions(),
        )
    }
}

private fun previewActions() = HomeActions(
    onAddQrClick = {},
    onQrClick = {},
    onQrDismiss = {},
    onSettingsClick = {},
    onHistoryClick = {},
)
