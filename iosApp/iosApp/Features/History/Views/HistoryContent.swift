import SharedLogic
import SwiftUI

struct HistoryContent: View {
    let state: HistoryUiState
    let actions: HistoryActions

    var body: some View {
        ScrollView {
            if !state.isLoading {
                VStack(spacing: Layout.sectionSpacing) {
                    MonthHeaderView(
                        year: Int(state.year),
                        month: Int(state.month),
                        canGoToNextMonth: state.canGoToNextMonth,
                        onPrevious: actions.onPreviousMonth,
                        onNext: actions.onNextMonth,
                    )
                    MonthStatsView(stats: state.monthStats)
                    CalendarGridView(
                        leadingBlankDays: Int(state.leadingBlankDays),
                        days: state.days,
                        onDaySelect: actions.onDaySelect,
                    )
                    DayDetailsView(
                        hasSelection: state.selectedDay != nil,
                        shifts: state.selectedDayShifts,
                        seriousMode: state.seriousMode,
                        onDeleteShift: actions.onDeleteShift,
                    )
                    if state.totals.shiftCount > 0 {
                        let count = Int(state.totals.shiftCount)
                        Text(state.seriousMode ? "history_lifetime_shifts \(count)" : "history_lifetime_galleys \(count)")
                            .font(.footnote.weight(.medium))
                            .foregroundStyle(.secondary)
                    }
                }
                .padding()
            }
        }
        .background(Color(.appBackground))
        .navigationTitle("history_title")
        .navigationBarTitleDisplayMode(.inline)
    }
}

private enum Layout {
    static let sectionSpacing: Double = 20
}

#Preview("Default") {
    NavigationStack {
        HistoryContent(state: .preview, actions: .preview)
    }
}

#Preview("Greek, large text") {
    NavigationStack {
        HistoryContent(state: .preview, actions: .preview)
    }
    .environment(\.locale, Locale(identifier: "el"))
    .dynamicTypeSize(.accessibility2)
}
