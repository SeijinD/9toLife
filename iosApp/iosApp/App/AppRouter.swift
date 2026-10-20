import Observation

/// Navigation state at the scene root, so screens that redraw every second never rebuild their destinations.
@MainActor
@Observable
final class AppRouter {
    var path: [AppRoute] = []
    var sheet: AppSheet?
    var isScannerPresented = false
    /// Set by the QR shortcut; Home clears it once it has asked to clock in or out.
    var isQuickQrPending = false

    /// A sheet must finish dismissing before a full-screen cover can appear.
    private var opensScannerAfterSheet = false

    func openHistory() {
        path.append(.history)
    }

    func openSettings() {
        sheet = .settings
    }

    func openScanner() {
        isScannerPresented = true
    }

    /// The QR shortcut: back to Home with nothing on top, then Home opens the QR.
    func openQuickQr() {
        path = []
        sheet = nil
        isScannerPresented = false
        isQuickQrPending = true
    }

    func onQuickQrHandled() {
        isQuickQrPending = false
    }

    func openScannerFromSheet() {
        opensScannerAfterSheet = true
        sheet = nil
    }

    func onSheetDismissed() {
        guard opensScannerAfterSheet else { return }
        opensScannerAfterSheet = false
        isScannerPresented = true
    }
}
