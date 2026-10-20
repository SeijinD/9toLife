import SharedLogic
import SwiftUI

struct HomeContent: View {
    let state: HomeUiState
    let overlay: HomeOverlay
    let actions: HomeActions

    var body: some View {
        ZStack {
            HomeBackgroundView(imagePath: state.backgroundImagePath)

            VStack(spacing: 0) {
                Spacer()
                if !state.isLoading {
                    HomeCenterView(state: state, actions: actions)
                        .transition(.opacity.combined(with: .scale(scale: Layout.enterScale)))
                }
                Spacer()
                TotalsLineView(totals: state.totals, seriousMode: state.seriousMode)
            }
            .padding(.bottom, Layout.bottomPadding)

            if state.isQrExpanded, let payload = state.qrPayload {
                ExpandedQrView(payload: payload, onDismiss: actions.onQrDismiss)
                    .transition(.opacity.combined(with: .scale(scale: Layout.enterScale)))
                    .zIndex(1)
            }

            if overlay.isCelebrating {
                FreedomCelebrationView(onFinished: actions.onCelebrationFinished)
                    .transition(.opacity)
                    .zIndex(2)
            }
        }
        .overlay(alignment: .bottom) {
            if let banner = overlay.banner {
                HomeBannerView(banner: banner, seriousMode: state.seriousMode, onUndo: actions.onUndo)
                    .padding(.horizontal)
                    .padding(.bottom, Layout.bannerPadding)
                    .transition(.move(edge: .bottom).combined(with: .opacity))
            }
        }
        .animation(.snappy, value: state.isQrExpanded)
        .animation(.snappy, value: state.qrPayload)
        .animation(.snappy, value: overlay)
        .toolbar {
            ToolbarItem(placement: .topBarLeading) {
                Button("home_history_cd", systemImage: "calendar", action: actions.onHistory)
            }
            ToolbarItem(placement: .topBarTrailing) {
                Button("home_settings_cd", systemImage: "gearshape", action: actions.onSettings)
            }
        }
        .toolbarVisibility(state.isQrExpanded ? .hidden : .automatic, for: .navigationBar)
    }
}

private enum Layout {
    static let enterScale: Double = 0.92
    static let bottomPadding: Double = 16
    static let bannerPadding: Double = 12
}

#Preview("Default") {
    NavigationStack {
        HomeContent(state: .preview, overlay: HomeOverlay(), actions: .preview)
    }
}

#Preview("Greek, large text") {
    NavigationStack {
        HomeContent(state: .preview, overlay: HomeOverlay(), actions: .preview)
    }
    .environment(\.locale, Locale(identifier: "el"))
    .dynamicTypeSize(.accessibility2)
}
