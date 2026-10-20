package com.seijind.ninetolife.ui.history.model

import com.seijind.ninetolife.domain.shift.model.CompletedShift
import com.seijind.ninetolife.domain.shift.model.ShiftTotals

data class HistoryUiState(
    val isLoading: Boolean = true,
    val year: Int = 0,
    /** 1 to 12. */
    val month: Int = 0,
    /** Empty cells before day 1 in a Monday-first week. */
    val leadingBlankDays: Int = 0,
    val days: List<CalendarDay> = emptyList(),
    val selectedDay: Int? = null,
    val selectedDayShifts: List<CompletedShift> = emptyList(),
    val monthStats: MonthStats = MonthStats(),
    val totals: ShiftTotals = ShiftTotals(),
    val canGoToNextMonth: Boolean = false,
    val seriousMode: Boolean = false,
)

data class CalendarDay(
    val dayOfMonth: Int,
    val shiftCount: Int,
    val hasOvertime: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
)

data class MonthStats(
    val shiftCount: Int = 0,
    val workedMinutes: Long = 0,
    val overtimeMinutes: Long = 0,
)

sealed interface HistoryEvent {
    /** A shift was deleted; the UI offers undo for exactly this one. */
    data class ShiftDeleted(val shift: CompletedShift) : HistoryEvent
}
