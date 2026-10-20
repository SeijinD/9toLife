import SharedLogic
import SwiftUI

struct RootView: View {
    let dependencies: IosDependencies
    let shiftAlerts: ShiftAlertsIOS
    let appModel: AppModel

    @State private var router = AppRouter()
    @State private var isSplashVisible = true

    var body: some View {
        ZStack {
            NavigationStack(path: $router.path) {
                ModelHost(makeModel: { HomeModel(owner: dependencies.homeViewModel()) }) { model in
                    HomeScreen(
                        model: model,
                        onOpenScanner: router.openScanner,
                        onOpenSettings: router.openSettings,
                        onOpenHistory: router.openHistory,
                        isQuickQrPending: router.isQuickQrPending,
                        onQuickQrHandled: router.onQuickQrHandled,
                    )
                }
                .navigationDestination(for: AppRoute.self) { route in
                    switch route {
                    case .history:
                        ModelHost(makeModel: { HistoryModel(owner: dependencies.historyViewModel()) }) { model in
                            HistoryScreen(model: model)
                        }
                    }
                }
            }
            .sheet(item: $router.sheet, onDismiss: router.onSheetDismissed) { sheet in
                switch sheet {
                case .settings:
                    ModelHost(makeModel: { SettingsModel(owner: dependencies.settingsViewModel()) }) { model in
                        SettingsScreen(model: model, onOpenScanner: router.openScannerFromSheet)
                    }
                    .appEnvironment(appModel)
                }
            }
            .fullScreenCover(isPresented: $router.isScannerPresented) {
                ModelHost(makeModel: { ScannerModel(owner: dependencies.scannerViewModel()) }) { model in
                    ScannerScreen(model: model)
                }
                .appEnvironment(appModel)
            }

            if isSplashVisible {
                SplashView(
                    isReady: appModel.state.isReady,
                    seriousMode: appModel.state.seriousMode,
                    onFinished: { isSplashVisible = false },
                )
                .transition(.opacity)
                .zIndex(1)
            }
        }
        .animation(.easeOut(duration: Motion.splashFade), value: isSplashVisible)
        .appEnvironment(appModel)
        .task { await appModel.observe() }
        .onChange(of: appModel.state.themeMode, initial: true) { _, mode in
            WindowAppearance.apply(mode)
        }
        .onChange(of: appModel.locale, initial: true) { _, locale in
            QuickQrShortcut.register(locale: locale)
        }
        // `initial` catches a tap that launched the app before this view existed.
        .onChange(of: QuickQrInbox.shared.isPending, initial: true) { _, isPending in
            guard isPending else { return }
            QuickQrInbox.shared.take()
            // At the door there is no time for the splash.
            isSplashVisible = false
            router.openQuickQr()
        }
        .task { await shiftAlerts.restore(using: dependencies.restoreShiftAlertsUseCase()) }
        .onChange(of: appModel.state.language) { _, language in
            AppLanguagePreference.apply(language: language, shiftAlerts: shiftAlerts)
            // A running shift's notifications and Live Activity carry their text, so they are re-posted.
            Task { await shiftAlerts.restore(using: dependencies.restoreShiftAlertsUseCase()) }
        }
    }
}

private extension View {
    /// Language and haptics from the settings. Applied again inside each sheet and cover,
    /// because an already presented one does not pick up later changes from the root.
    func appEnvironment(_ appModel: AppModel) -> some View {
        environment(\.locale, appModel.locale)
            .environment(\.hapticsEnabled, appModel.state.hapticsEnabled)
    }
}

private enum Motion {
    static let splashFade: Double = 0.28
}
