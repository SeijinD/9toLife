import SwiftUI

/// Counts down to the end and then, on its own, up into overtime, with a "+" like the app shows.
struct ShiftTimerText: View {
    let endsAt: Date
    let isOvertime: Bool

    var body: some View {
        HStack(spacing: 0) {
            if isOvertime {
                Text(verbatim: "+")
            }
            Text(endsAt, style: .timer)
        }
        .monospacedDigit()
        .multilineTextAlignment(.trailing)
    }
}
