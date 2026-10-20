import SharedLogic
import UIKit

/// The theme is set on the windows instead of with `preferredColorScheme`: going back to "system"
/// with `preferredColorScheme(nil)` does not release a sheet that is already open.
@MainActor
enum WindowAppearance {
    static func apply(_ mode: ThemeMode) {
        let style: UIUserInterfaceStyle = switch mode {
        case .system: .unspecified
        case .light: .light
        case .dark: .dark
        }
        let windows = UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap(\.windows)
        for window in windows {
            window.overrideUserInterfaceStyle = style
        }
    }
}
