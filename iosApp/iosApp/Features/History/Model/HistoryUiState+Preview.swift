import SharedLogic

#if DEBUG
extension HistoryUiState {
    static let preview = HistoryUiState(
        isLoading: false,
        year: 2026,
        month: 10,
        leadingBlankDays: 3,
        days: (1...31).map { (day: Int) -> CalendarDay in
            let isWorkday = day % 7 >= 1 && day % 7 <= 5 && day <= 17
            return CalendarDay(
                dayOfMonth: Int32(day),
                shiftCount: isWorkday ? 1 : 0,
                hasOvertime: day == 9,
                isToday: day == 17,
                isSelected: day == 16,
            )
        },
        selectedDay: KotlinInt(int: 16),
        selectedDayShifts: [
            CompletedShift(id: 1, startedAtEpochMillis: 1_760_601_600_000, endedAtEpochMillis: 1_760_635_800_000, plannedMinutes: 480),
        ],
        monthStats: MonthStats(shiftCount: 11, workedMinutes: 5_430, overtimeMinutes: 150),
        totals: ShiftTotals(shiftCount: 37, overtimeMinutes: 750),
        canGoToNextMonth: false,
        seriousMode: false,
    )
}
#endif
