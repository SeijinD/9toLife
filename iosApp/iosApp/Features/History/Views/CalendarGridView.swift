import SharedLogic
import SwiftUI

struct CalendarGridView: View {
    let leadingBlankDays: Int
    let days: [CalendarDay]
    let onDaySelect: (Int) -> Void

    @Environment(\.locale) private var locale
    @Environment(\.calendar) private var calendar

    private let columns = Array(repeating: GridItem(.flexible(), spacing: 0), count: Layout.daysPerWeek)

    var body: some View {
        LazyVGrid(columns: columns, spacing: Layout.rowSpacing) {
            ForEach(Array(weekdaySymbols.enumerated()), id: \.offset) { _, symbol in
                Text(verbatim: symbol)
                    .font(.caption.weight(.medium))
                    .foregroundStyle(.secondary)
            }
            ForEach(0..<leadingBlankDays, id: \.self) { _ in
                Color.clear.frame(height: 1)
            }
            ForEach(days, id: \.dayOfMonth) { day in
                DayCellView(day: day, onSelect: { onDaySelect(Int(day.dayOfMonth)) })
            }
        }
    }

    /// Monday first, as the shared calendar lays out the month.
    private var weekdaySymbols: [String] {
        var calendar = calendar
        calendar.locale = locale
        let symbols = calendar.veryShortStandaloneWeekdaySymbols
        return Array(symbols[1...] + symbols[..<1])
    }
}

private enum Layout {
    static let daysPerWeek = 7
    static let rowSpacing: Double = 6
}
