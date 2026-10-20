import SharedLogic
import SwiftUI

struct DayCellView: View {
    let day: CalendarDay
    let onSelect: () -> Void

    var body: some View {
        Button(action: onSelect) {
            VStack(spacing: Layout.dotSpacing) {
                Text(verbatim: "\(day.dayOfMonth)")
                    .font(.body)
                    .foregroundStyle(day.isSelected ? Color.white : Color.primary)
                Circle()
                    .fill(dotColor)
                    .frame(width: Layout.dotSize, height: Layout.dotSize)
            }
            .frame(maxWidth: .infinity)
            .aspectRatio(1, contentMode: .fit)
            .background {
                if day.isSelected {
                    Circle().fill(Color.accentColor)
                } else if day.isToday {
                    Circle().stroke(Color.accentColor, lineWidth: Layout.todayStroke)
                }
            }
            .contentShape(.circle)
        }
        .buttonStyle(.plain)
        .accessibilityAddTraits(day.isSelected ? .isSelected : [])
    }

    private var dotColor: Color {
        if day.shiftCount == 0 { return .clear }
        if day.isSelected { return .white }
        return day.hasOvertime ? Color(.overtime) : .accentColor
    }
}

private enum Layout {
    static let dotSize: Double = 5
    static let dotSpacing: Double = 2
    static let todayStroke: Double = 1.5
}
