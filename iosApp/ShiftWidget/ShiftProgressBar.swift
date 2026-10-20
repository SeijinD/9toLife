import SwiftUI

struct ShiftProgressBar: View {
    let startedAt: Date
    let endsAt: Date

    var body: some View {
        ProgressView(timerInterval: startedAt...max(endsAt, startedAt), countsDown: false) {
            EmptyView()
        } currentValueLabel: {
            EmptyView()
        }
        .tint(Color(.brandBrass))
    }
}
