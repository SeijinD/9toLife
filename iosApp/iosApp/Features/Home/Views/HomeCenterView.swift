import SharedLogic
import SwiftUI

struct HomeCenterView: View {
    let state: HomeUiState
    let actions: HomeActions

    var body: some View {
        if let payload = state.qrPayload {
            VStack(spacing: Layout.spacing) {
                QrCardView(payload: payload, onTap: actions.onQrTap)
                if let shift = state.shift {
                    ShiftTimerView(shift: shift, seriousMode: state.seriousMode)
                } else {
                    Text(state.seriousMode ? "home_idle_hint_serious" : "home_idle_hint_fun")
                        .font(.headline)
                        .foregroundStyle(.secondary)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal)
                }
            }
        } else {
            AddQrPromptView(seriousMode: state.seriousMode, onAddQr: actions.onAddQr)
        }
    }
}

private enum Layout {
    static let spacing: Double = 32
}
