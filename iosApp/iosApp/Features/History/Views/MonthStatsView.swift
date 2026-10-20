import SharedLogic
import SwiftUI

struct MonthStatsView: View {
    let stats: MonthStats

    @Environment(\.locale) private var locale

    var body: some View {
        HStack(spacing: Layout.spacing) {
            StatCardView(value: "\(stats.shiftCount)", label: "history_stat_shifts")
            StatCardView(value: locale.duration(minutes: stats.workedMinutes), label: "history_stat_hours")
            StatCardView(value: locale.duration(minutes: stats.overtimeMinutes), label: "history_stat_overtime")
        }
    }
}

private enum Layout {
    static let spacing: Double = 8
}
