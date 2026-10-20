package com.seijind.ninetolife.ui.history.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seijind.ninetolife.domain.shift.model.CompletedShift
import com.seijind.ninetolife.domain.shift.model.ShiftTotals
import com.seijind.ninetolife.ui.history.model.CalendarDay
import com.seijind.ninetolife.ui.history.model.HistoryEvent
import com.seijind.ninetolife.ui.history.model.HistoryUiState
import com.seijind.ninetolife.ui.history.model.MonthStats
import com.seijind.ninetolife.usecase.settings.ObserveSettingsUseCase
import com.seijind.ninetolife.usecase.shift.DeleteShiftUseCase
import com.seijind.ninetolife.usecase.shift.ObserveShiftTotalsUseCase
import com.seijind.ninetolife.usecase.shift.ObserveShiftsStartedBetweenUseCase
import com.seijind.ninetolife.usecase.shift.RestoreShiftUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Instant

class HistoryViewModel(
    private val observeShiftsStartedBetween: ObserveShiftsStartedBetweenUseCase,
    private val observeShiftTotals: ObserveShiftTotalsUseCase,
    private val observeSettings: ObserveSettingsUseCase,
    private val deleteShift: DeleteShiftUseCase,
    private val restoreShift: RestoreShiftUseCase,
    private val clock: Clock,
) : ViewModel() {

    private val timeZone = TimeZone.currentSystemDefault()

    private val visibleMonth = MutableStateFlow(value = today().let { LocalDate(year = it.year, month = it.month, day = 1) })
    private val selectedDay = MutableStateFlow<Int?>(value = today().day)

    private val _uiState = MutableStateFlow(value = HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val _events = Channel<HistoryEvent>(capacity = Channel.BUFFERED)
    val events: Flow<HistoryEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                monthShifts(),
                selectedDay,
                observeShiftTotals(),
                observeSettings().map { it.seriousMode },
            ) { (firstOfMonth, shifts), day, totals, seriousMode ->
                buildState(
                    firstOfMonth = firstOfMonth,
                    shifts = shifts,
                    selectedDay = day,
                    totals = totals,
                    seriousMode = seriousMode,
                )
            }.collect { state -> _uiState.value = state }
        }
    }

    fun onPreviousMonthClick() {
        visibleMonth.update { it.minus(value = 1, unit = DateTimeUnit.MONTH) }
        selectedDay.value = null
    }

    fun onNextMonthClick() {
        if (!_uiState.value.canGoToNextMonth) return
        visibleMonth.update { it.plus(value = 1, unit = DateTimeUnit.MONTH) }
        selectedDay.value = null
    }

    fun onDayClick(dayOfMonth: Int) {
        selectedDay.update { current -> if (current == dayOfMonth) null else dayOfMonth }
    }

    fun onDeleteShift(shift: CompletedShift) {
        viewModelScope.launch {
            deleteShift(shift = shift)
            _events.trySend(element = HistoryEvent.ShiftDeleted(shift = shift))
        }
    }

    /** Takes the shift from the event, so a newer delete cannot replace what this undo restores. */
    fun onUndoDelete(shift: CompletedShift) {
        viewModelScope.launch { restoreShift(shift = shift) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun monthShifts() = visibleMonth.flatMapLatest { firstOfMonth ->
        val from = firstOfMonth.atStartOfDayIn(timeZone = timeZone)
        val to = firstOfMonth.plus(value = 1, unit = DateTimeUnit.MONTH).atStartOfDayIn(timeZone = timeZone)
        observeShiftsStartedBetween(
            fromEpochMillis = from.toEpochMilliseconds(),
            toEpochMillis = to.toEpochMilliseconds(),
        ).map { shifts -> firstOfMonth to shifts }
    }

    private fun buildState(
        firstOfMonth: LocalDate,
        shifts: List<CompletedShift>,
        selectedDay: Int?,
        totals: ShiftTotals,
        seriousMode: Boolean,
    ): HistoryUiState {
        // Read on every rebuild instead of once, so the first change after midnight marks the right day.
        val today = today()
        val shiftsByDay = shifts.groupBy { it.startedAtEpochMillis.toLocalDate().day }
        val daysInMonth = firstOfMonth.plus(value = 1, unit = DateTimeUnit.MONTH).minus(value = 1, unit = DateTimeUnit.DAY).day
        val isCurrentMonth = firstOfMonth.year == today.year && firstOfMonth.month == today.month
        val days = (1..daysInMonth).map { day ->
            val dayShifts = shiftsByDay[day].orEmpty()
            CalendarDay(
                dayOfMonth = day,
                shiftCount = dayShifts.size,
                hasOvertime = dayShifts.any { it.overtimeMinutes > 0 },
                isToday = isCurrentMonth && day == today.day,
                isSelected = day == selectedDay,
            )
        }
        return HistoryUiState(
            isLoading = false,
            year = firstOfMonth.year,
            month = firstOfMonth.month.number,
            leadingBlankDays = firstOfMonth.dayOfWeek.isoDayNumber - 1,
            days = days,
            selectedDay = selectedDay,
            selectedDayShifts = selectedDay?.let { shiftsByDay[it] }.orEmpty(),
            monthStats = MonthStats(
                shiftCount = shifts.size,
                workedMinutes = shifts.sumOf { it.workedMinutes },
                overtimeMinutes = shifts.sumOf { it.overtimeMinutes },
            ),
            totals = totals,
            canGoToNextMonth = firstOfMonth < LocalDate(year = today.year, month = today.month, day = 1),
            seriousMode = seriousMode,
        )
    }

    private fun today(): LocalDate = clock.todayIn(timeZone = timeZone)

    private fun Long.toLocalDate(): LocalDate =
        Instant.fromEpochMilliseconds(epochMilliseconds = this).toLocalDateTime(timeZone = timeZone).date
}
