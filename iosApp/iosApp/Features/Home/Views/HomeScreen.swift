import SharedLogic
import SwiftUI

struct HomeScreen: View {
    let model: HomeModel
    @State private var overlay = HomeOverlay()
    @State private var haptic = HapticTrigger()
    @Environment(\.hapticsEnabled) private var hapticsEnabled

    private let onOpenScanner: () -> Void
    private let onOpenSettings: () -> Void
    private let onOpenHistory: () -> Void
    private let isQuickQrPending: Bool
    private let onQuickQrHandled: () -> Void

    init(
        model: HomeModel,
        onOpenScanner: @escaping () -> Void,
        onOpenSettings: @escaping () -> Void,
        onOpenHistory: @escaping () -> Void,
        isQuickQrPending: Bool,
        onQuickQrHandled: @escaping () -> Void,
    ) {
        self.model = model
        self.onOpenScanner = onOpenScanner
        self.onOpenSettings = onOpenSettings
        self.onOpenHistory = onOpenHistory
        self.isQuickQrPending = isQuickQrPending
        self.onQuickQrHandled = onQuickQrHandled
    }

    var body: some View {
        HomeContent(
            state: model.state,
            overlay: overlay,
            actions: HomeActions(
                onAddQr: model.onAddQr,
                onQrTap: model.onQrTap,
                onQrDismiss: model.onQrDismiss,
                onSettings: onOpenSettings,
                onHistory: onOpenHistory,
                onUndo: undo,
                onCelebrationFinished: finishCelebration,
            ),
        )
        .hapticFeedback(haptic, enabled: hapticsEnabled)
        .task { await model.observe() }
        .task(id: model.state.backgroundImagePath) { await model.deleteUnusedBackgrounds() }
        .onChange(of: isQuickQrPending, initial: true) { _, pending in
            guard pending else { return }
            onQuickQrHandled()
            model.onQuickQrRequest()
        }
        .task {
            for await event in model.events {
                handle(event)
            }
        }
        .task(id: overlay.banner?.id) {
            guard overlay.banner != nil else { return }
            do {
                try await Task.sleep(for: UndoBannerView.displayDuration)
            } catch {
                return
            }
            expireBanner()
        }
    }

    private func handle(_ event: HomeEvent) {
        switch onEnum(of: event) {
        case .openScanner:
            onOpenScanner()
        case .shiftStarted:
            haptic.fire(.success)
            overlay.banner = HomeBanner(kind: .shiftStarted)
        case .shiftEnded(let ended):
            haptic.fire(.success)
            overlay.banner = HomeBanner(kind: .shiftEnded(workedMinutes: ended.workedMinutes))
        case .undoDone:
            haptic.fire(.warning)
            overlay.banner = HomeBanner(kind: .undoDone)
        case .freedom:
            haptic.fire(.celebration)
            if model.state.seriousMode {
                overlay.banner = HomeBanner(kind: .shiftTimeCompleted)
            } else {
                overlay.isCelebrating = true
            }
        }
    }

    private func undo() {
        overlay.banner = nil
        model.onUndo()
    }

    private func expireBanner() {
        if overlay.banner?.offersUndo == true {
            model.onUndoExpired()
        }
        overlay.banner = nil
    }

    private func finishCelebration() {
        overlay.isCelebrating = false
    }
}
