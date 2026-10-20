import SharedLogic
import SwiftUI

struct ShiftTimerView: View {
    let shift: ShiftProgress
    let seriousMode: Bool

    @Environment(\.locale) private var locale

    var body: some View {
        VStack(spacing: Layout.spacing) {
            if shift.isOvertime {
                Text("home_timer_overtime_label")
                    .font(.subheadline.weight(.semibold))
                    .foregroundStyle(Color.accentColor)
            }
            Text(verbatim: shift.isOvertime
                ? "+\(locale.clock(millis: shift.overtimeMillis))"
                : locale.clock(millis: shift.remainingMillis))
                .font(.system(.largeTitle, design: .rounded, weight: .semibold))
                .monospacedDigit()
                .contentTransition(.numericText(countsDown: !shift.isOvertime))
            ShiftProgressBarView(progress: Double(shift.progress))
            Text(moodKey)
                .font(.headline)
                .multilineTextAlignment(.center)
                .contentTransition(.opacity)
            Text("home_timer_ends_at \(locale.timeOfDay(epochMillis: shift.endsAtEpochMillis))")
                .font(.subheadline)
                .foregroundStyle(.secondary)
        }
        .padding(.horizontal)
    }

    private var moodKey: LocalizedStringKey {
        if seriousMode {
            return shift.mood == .overtime ? "home_mood_overtime_serious" : "home_mood_serious"
        }
        switch shift.mood {
        case .justStarted: return "home_mood_just_started"
        case .warmingUp: return "home_mood_warming_up"
        case .halfway: return "home_mood_halfway"
        case .homeStretch: return "home_mood_home_stretch"
        case .almostFree: return "home_mood_almost_free"
        case .overtime: return "home_mood_overtime"
        }
    }
}

private enum Layout {
    static let spacing: Double = 8
}
