import ActivityKit
import SwiftUI
import WidgetKit

/// Lock screen and Dynamic Island for a running shift. The system animates the timer and the bar,
/// so the app does not need to push updates every second.
struct ShiftLiveActivityWidget: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: ShiftActivityAttributes.self) { context in
            ShiftLockScreenView(context: context)
                .activityBackgroundTint(Color(.brandNavy))
                .activitySystemActionForegroundColor(Color(.brandBrass))
        } dynamicIsland: { context in
            DynamicIsland {
                DynamicIslandExpandedRegion(.leading) {
                    ShiftClockIcon()
                }
                DynamicIslandExpandedRegion(.trailing) {
                    ShiftTimerText(endsAt: context.attributes.endsAt, isOvertime: context.isStale)
                        .font(.title3.weight(.semibold))
                }
                DynamicIslandExpandedRegion(.bottom) {
                    VStack(alignment: .leading, spacing: ShiftWidgetLayout.spacing) {
                        Text(verbatim: context.headline)
                            .font(.subheadline.weight(.semibold))
                        ShiftProgressBar(startedAt: context.attributes.startedAt, endsAt: context.attributes.endsAt)
                    }
                }
            } compactLeading: {
                ShiftClockIcon()
            } compactTrailing: {
                ShiftTimerText(endsAt: context.attributes.endsAt, isOvertime: context.isStale)
                    .frame(maxWidth: ShiftWidgetLayout.compactTimerWidth)
            } minimal: {
                ShiftClockIcon()
            }
            .keylineTint(Color(.brandBrass))
        }
    }
}

private struct ShiftClockIcon: View {
    var body: some View {
        Image(systemName: "clock.fill")
            .foregroundStyle(Color(.brandBrass))
    }
}
