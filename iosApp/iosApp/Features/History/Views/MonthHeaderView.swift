import SwiftUI

struct MonthHeaderView: View {
    let year: Int
    let month: Int
    let canGoToNextMonth: Bool
    let onPrevious: () -> Void
    let onNext: () -> Void

    @Environment(\.locale) private var locale
    @Environment(\.calendar) private var calendar

    var body: some View {
        HStack {
            Button("history_previous_month_cd", systemImage: "chevron.left", action: onPrevious)
                .labelStyle(.iconOnly)
            Text(verbatim: title)
                .font(.title3.weight(.semibold))
                .frame(maxWidth: .infinity)
            Button("history_next_month_cd", systemImage: "chevron.right", action: onNext)
                .labelStyle(.iconOnly)
                .disabled(!canGoToNextMonth)
        }
    }

    private var title: String {
        guard let date = calendar.date(from: DateComponents(year: year, month: month, day: 1)) else { return "" }
        return date.formatted(Date.FormatStyle(locale: locale).month(.wide).year()).capitalized(with: locale)
    }
}
