package com.seijind.ninetolife.ui.history.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seijind.ninetolife.R
import com.seijind.ninetolife.domain.shift.model.CompletedShift
import com.seijind.ninetolife.domain.shift.model.ShiftTotals
import com.seijind.ninetolife.ui.common.SwipeToDeleteRow
import com.seijind.ninetolife.ui.common.format.formatDuration
import com.seijind.ninetolife.ui.common.format.formatTimeOfDay
import com.seijind.ninetolife.ui.history.model.CalendarDay
import com.seijind.ninetolife.ui.history.model.HistoryActions
import com.seijind.ninetolife.ui.history.model.HistoryUiState
import com.seijind.ninetolife.ui.history.model.MonthStats
import com.seijind.ninetolife.ui.theme.AppTheme
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

private val HorizontalPadding = 20.dp
private val SectionSpacing = 20.dp
private val ItemSpacing = 8.dp
private val StatCardPadding = 14.dp
private val DayDotSize = 5.dp
private val DayDotSpacing = 2.dp
private val TodayBorder = 1.5.dp
private const val MonthTitlePattern = "LLLL yyyy"
private const val DaysPerWeek = 7

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HistoryContent(
    uiState: HistoryUiState,
    actions: HistoryActions,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.history_title)) },
                navigationIcon = {
                    IconButton(onClick = actions.onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.history_back_cd),
                        )
                    }
                },
            )
        },
    ) { padding ->
        if (uiState.isLoading) return@Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = padding)
                .verticalScroll(state = rememberScrollState())
                .padding(horizontal = HorizontalPadding, vertical = ItemSpacing),
            verticalArrangement = Arrangement.spacedBy(space = SectionSpacing),
        ) {
            MonthHeader(
                year = uiState.year,
                month = uiState.month,
                canGoToNextMonth = uiState.canGoToNextMonth,
                onPreviousClick = actions.onPreviousMonthClick,
                onNextClick = actions.onNextMonthClick,
            )
            MonthStatsRow(stats = uiState.monthStats)
            CalendarGrid(leadingBlankDays = uiState.leadingBlankDays, days = uiState.days, onDayClick = actions.onDayClick)
            DayDetails(
                selectedDay = uiState.selectedDay,
                shifts = uiState.selectedDayShifts,
                seriousMode = uiState.seriousMode,
                onDeleteShift = actions.onDeleteShift,
            )
            if (uiState.totals.shiftCount > 0) {
                Text(
                    text = pluralStringResource(
                        id = if (uiState.seriousMode) R.plurals.history_lifetime_shifts else R.plurals.history_lifetime_galleys,
                        count = uiState.totals.shiftCount,
                        uiState.totals.shiftCount,
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun MonthHeader(
    year: Int,
    month: Int,
    canGoToNextMonth: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    val locale = LocalConfiguration.current.locales[0]
    val title = remember(year, month, locale) {
        YearMonth.of(year, month)
            .format(DateTimeFormatter.ofPattern(MonthTitlePattern, locale))
            .replaceFirstChar { it.titlecase(locale = locale) }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onPreviousClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(id = R.string.history_previous_month_cd),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(weight = 1f),
        )
        IconButton(onClick = onNextClick, enabled = canGoToNextMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(id = R.string.history_next_month_cd),
            )
        }
    }
}

@Composable
private fun MonthStatsRow(stats: MonthStats) {
    Row(horizontalArrangement = Arrangement.spacedBy(space = ItemSpacing)) {
        StatCard(label = stringResource(id = R.string.history_stat_shifts), value = stats.shiftCount.toString())
        StatCard(label = stringResource(id = R.string.history_stat_hours), value = formatDuration(minutes = stats.workedMinutes))
        StatCard(label = stringResource(id = R.string.history_stat_overtime), value = formatDuration(minutes = stats.overtimeMinutes))
    }
}

@Composable
private fun RowScope.StatCard(
    label: String,
    value: String,
) {
    Card(
        modifier = Modifier.weight(weight = 1f),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(modifier = Modifier.padding(all = StatCardPadding)) {
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    leadingBlankDays: Int,
    days: List<CalendarDay>,
    onDayClick: (Int) -> Unit,
) {
    val locale = LocalConfiguration.current.locales[0]
    val weeks = remember(leadingBlankDays, days) {
        (List<CalendarDay?>(size = leadingBlankDays) { null } + days)
            .chunked(size = DaysPerWeek)
            .map { week -> week + List(size = DaysPerWeek - week.size) { null } }
    }
    val weekDays = remember(locale) { DayOfWeek.entries.map { it.getDisplayName(TextStyle.NARROW, locale) } }
    Column(verticalArrangement = Arrangement.spacedBy(space = ItemSpacing)) {
        Row {
            weekDays.forEach { name ->
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(weight = 1f),
                )
            }
        }
        weeks.forEach { week ->
            Row {
                week.forEach { day ->
                    Box(
                        modifier = Modifier
                            .weight(weight = 1f)
                            .aspectRatio(ratio = 1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (day != null) DayCell(day = day, onClick = { onDayClick(day.dayOfMonth) })
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: CalendarDay,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val background = if (day.isSelected) colors.primary else Color.Transparent
    val textColor = if (day.isSelected) colors.onPrimary else colors.onSurface
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = DayDotSpacing)
            .clip(shape = CircleShape)
            .background(color = background)
            .then(if (day.isToday && !day.isSelected) Modifier.border(width = TodayBorder, color = colors.primary, shape = CircleShape) else Modifier)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = day.dayOfMonth.toString(), style = MaterialTheme.typography.bodyLarge, color = textColor)
        Spacer(modifier = Modifier.size(size = DayDotSpacing))
        val dotColor = when {
            day.shiftCount == 0 -> Color.Transparent
            day.isSelected -> colors.onPrimary
            day.hasOvertime -> colors.error
            else -> colors.primary
        }
        Box(modifier = Modifier.size(size = DayDotSize).background(color = dotColor, shape = CircleShape))
    }
}

@Composable
private fun DayDetails(
    selectedDay: Int?,
    shifts: List<CompletedShift>,
    seriousMode: Boolean,
    onDeleteShift: (CompletedShift) -> Unit,
) {
    when {
        selectedDay == null -> DetailHint(text = stringResource(id = R.string.history_select_day))
        shifts.isEmpty() -> DetailHint(
            text = stringResource(id = if (seriousMode) R.string.history_day_empty_serious else R.string.history_day_empty_fun),
        )
        else -> Column(verticalArrangement = Arrangement.spacedBy(space = ItemSpacing)) {
            shifts.forEach { shift ->
                key(shift.id) {
                    SwipeToDeleteRow(
                        onDelete = { onDeleteShift(shift) },
                        deleteLabel = stringResource(id = R.string.history_delete_shift),
                        shape = CardDefaults.shape,
                    ) {
                        ShiftRow(shift = shift, seriousMode = seriousMode)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailHint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun ShiftRow(shift: CompletedShift, seriousMode: Boolean) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = StatCardPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(weight = 1f)) {
                Text(
                    text = stringResource(
                        id = R.string.history_shift_time_range,
                        formatTimeOfDay(epochMillis = shift.startedAtEpochMillis),
                        formatTimeOfDay(epochMillis = shift.endedAtEpochMillis),
                    ),
                    style = MaterialTheme.typography.titleMedium,
                )
                if (shift.overtimeMinutes > 0) {
                    Text(
                        text = stringResource(id = R.string.history_shift_overtime, formatDuration(minutes = shift.overtimeMinutes)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                if (shift.earlyLeaveMinutes > 0) {
                    Text(
                        text = stringResource(
                            id = if (seriousMode) R.string.history_shift_early_serious else R.string.history_shift_early_fun,
                            formatDuration(minutes = shift.earlyLeaveMinutes),
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
            Text(
                text = formatDuration(minutes = shift.workedMinutes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
@Preview
@Preview(locale = "el", fontScale = 1.5f)
private fun HistoryContentPreview() {
    val dayStart = 1_760_680_800_000L
    val hour = 3_600_000L
    AppTheme(darkTheme = false) {
        HistoryContent(
            uiState = HistoryUiState(
                isLoading = false,
                year = 2026,
                month = 10,
                leadingBlankDays = 3,
                days = (1..31).map { day ->
                    CalendarDay(
                        dayOfMonth = day,
                        shiftCount = if (day % 7 in 1..5 && day <= 17) 1 else 0,
                        hasOvertime = day == 9,
                        isToday = day == 17,
                        isSelected = day == 16,
                    )
                },
                selectedDay = 16,
                selectedDayShifts = listOf(
                    CompletedShift(id = 1, startedAtEpochMillis = dayStart + 9 * hour, endedAtEpochMillis = dayStart + 18 * hour + hour / 2, plannedMinutes = 480),
                ),
                monthStats = MonthStats(shiftCount = 11, workedMinutes = 5_430, overtimeMinutes = 150),
                totals = ShiftTotals(shiftCount = 37, overtimeMinutes = 750),
            ),
            actions = HistoryActions(onBackClick = {}, onPreviousMonthClick = {}, onNextMonthClick = {}, onDayClick = {}, onDeleteShift = {}),
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}
