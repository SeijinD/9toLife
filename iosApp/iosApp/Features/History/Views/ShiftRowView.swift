import SharedLogic
import SwiftUI

struct ShiftRowView: View {
    let shift: CompletedShift
    let seriousMode: Bool

    @Environment(\.locale) private var locale

    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: Layout.spacing) {
                Text(verbatim: "\(locale.timeOfDay(epochMillis: shift.startedAtEpochMillis)) - \(locale.timeOfDay(epochMillis: shift.endedAtEpochMillis))")
                    .font(.headline)
                if shift.overtimeMinutes > 0 {
                    Text("history_shift_overtime \(locale.duration(minutes: shift.overtimeMinutes))")
                        .font(.subheadline)
                        .foregroundStyle(Color(.overtime))
                }
                if shift.earlyLeaveMinutes > 0 {
                    let duration = locale.duration(minutes: shift.earlyLeaveMinutes)
                    Text(seriousMode ? "history_shift_early_serious \(duration)" : "history_shift_early_fun \(duration)")
                        .font(.subheadline)
                        .foregroundStyle(Color(.earlyLeave))
                }
            }
            Spacer()
            Text(verbatim: locale.duration(minutes: shift.workedMinutes))
                .font(.headline)
        }
        .padding(Layout.padding)
        .background(Color(.appSurface), in: .rect(cornerRadius: Layout.cornerRadius))
        .accessibilityElement(children: .combine)
    }
}

private enum Layout {
    static let spacing: Double = 2
    static let padding: Double = 14
    static let cornerRadius: Double = 12
}
