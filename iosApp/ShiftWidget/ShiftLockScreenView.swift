import ActivityKit
import SwiftUI
import WidgetKit

struct ShiftLockScreenView: View {
    let context: ActivityViewContext<ShiftActivityAttributes>

    var body: some View {
        VStack(alignment: .leading, spacing: ShiftWidgetLayout.spacing) {
            HStack {
                VStack(alignment: .leading, spacing: ShiftWidgetLayout.textSpacing) {
                    Text(verbatim: context.headline)
                        .font(.headline)
                    Text(verbatim: context.state.subtitle)
                        .font(.subheadline)
                        .foregroundStyle(.white.opacity(ShiftWidgetLayout.secondaryOpacity))
                }
                Spacer()
                ShiftTimerText(endsAt: context.attributes.endsAt, isOvertime: context.isStale)
                    .font(.title2.weight(.semibold))
            }
            ShiftProgressBar(startedAt: context.attributes.startedAt, endsAt: context.attributes.endsAt)
        }
        .foregroundStyle(.white)
        .padding()
    }
}

extension ActivityViewContext<ShiftActivityAttributes> {
    var headline: String { isStale ? state.overtimeLabel : state.title }
}
