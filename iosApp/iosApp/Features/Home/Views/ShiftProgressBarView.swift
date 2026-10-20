import SwiftUI

/// One rounded bar that fills up, the same shape as the Android one.
struct ShiftProgressBarView: View {
    let progress: Double

    var body: some View {
        Capsule()
            .fill(.quaternary)
            .overlay(alignment: .leading) {
                Capsule()
                    .fill(Color.accentColor)
                    .frame(width: Layout.width * min(max(progress, 0), 1))
            }
            .frame(width: Layout.width, height: Layout.height)
            .clipShape(.capsule)
            .accessibilityRepresentation {
                ProgressView(value: progress)
            }
    }
}

private enum Layout {
    static let width: Double = 240
    static let height: Double = 8
}

#Preview {
    ShiftProgressBarView(progress: 0.4)
}
