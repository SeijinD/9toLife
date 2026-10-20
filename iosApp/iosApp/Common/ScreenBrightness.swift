import UIKit

/// SwiftUI has no brightness API, so this goes through the window scene's screen.
@MainActor
enum ScreenBrightness {
    private static var savedBrightness: Double?

    static func boost() {
        guard let screen = currentScreen, savedBrightness == nil else { return }
        savedBrightness = screen.brightness
        screen.brightness = 1
    }

    static func restore() {
        guard let screen = currentScreen, let savedBrightness else { return }
        screen.brightness = savedBrightness
        self.savedBrightness = nil
    }

    private static var currentScreen: UIScreen? {
        UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .first { $0.activationState == .foregroundActive }?
            .screen
    }
}
