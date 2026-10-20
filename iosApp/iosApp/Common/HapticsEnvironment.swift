import SwiftUI

extension EnvironmentValues {
    /// Mirrors the haptics setting so every view can gate `sensoryFeedback` on it.
    @Entry var hapticsEnabled: Bool = true
}
