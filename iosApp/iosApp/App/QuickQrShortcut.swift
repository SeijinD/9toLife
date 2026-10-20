import UIKit

/// The "clock in/out with the QR" Home Screen quick action (long press on the app icon).
/// Registered from code so its title follows the in-app language; it shows after the first launch.
@MainActor
enum QuickQrShortcut {
    static let type = "com.seijind.ninetolife.openQr"

    static func register(locale: Locale) {
        UIApplication.shared.shortcutItems = [
            UIApplicationShortcutItem(
                type: type,
                localizedTitle: String(localized: "quick_qr_shortcut_title", bundle: .localized(for: locale)),
                localizedSubtitle: nil,
                icon: UIApplicationShortcutIcon(systemImageName: "qrcode"),
            ),
        ]
    }
}
