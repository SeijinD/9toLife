import AppIntents
import Observation

/// Shared by the app and the ShiftWidget extension. The control runs this intent, which opens the app
/// and runs `perform()` in the app process, where RootView picks up the request.
struct OpenQuickQrIntent: AppIntent {
    static let title: LocalizedStringResource = "quick_qr_label"
    // iOS 18 only knows `openAppWhenRun`; from iOS 26 the system reads `supportedModes` instead.
    static let openAppWhenRun = true

    @available(iOS 26.0, *)
    static var supportedModes: IntentModes { .foreground(.immediate) }

    @MainActor
    func perform() async throws -> some IntentResult {
        QuickQrInbox.shared.request()
        return .result()
    }
}

/// Holds a QR shortcut tap until RootView takes it.
@MainActor
@Observable
final class QuickQrInbox {
    static let shared = QuickQrInbox()

    private(set) var isPending = false

    func request() {
        isPending = true
    }

    func take() {
        isPending = false
    }
}
