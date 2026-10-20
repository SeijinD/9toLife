import SharedLogic
import SwiftUI

struct DayDetailsView: View {
    let hasSelection: Bool
    let shifts: [CompletedShift]
    let seriousMode: Bool
    let onDeleteShift: (CompletedShift) -> Void

    var body: some View {
        if !hasSelection {
            HistoryHintView(key: "history_select_day")
        } else if shifts.isEmpty {
            HistoryHintView(key: seriousMode ? "history_day_empty_serious" : "history_day_empty_fun")
        } else {
            VStack(spacing: Layout.spacing) {
                ForEach(shifts, id: \.id) { shift in
                    SwipeToDeleteRow(onDelete: { onDeleteShift(shift) }) {
                        ShiftRowView(shift: shift, seriousMode: seriousMode)
                    }
                }
            }
        }
    }
}

private enum Layout {
    static let spacing: Double = 8
}

private struct HistoryHintView: View {
    let key: LocalizedStringKey

    var body: some View {
        Text(key)
            .foregroundStyle(.secondary)
            .multilineTextAlignment(.center)
            .frame(maxWidth: .infinity)
    }
}
