import SwiftUI

/// Equatable stand-in for `SensoryFeedback`, usable as a `sensoryFeedback` trigger.
enum HapticKind: Equatable {
    case selection
    case success
    case warning
    case celebration

    var feedback: SensoryFeedback {
        switch self {
        case .selection: .selection
        case .success: .success
        case .warning: .warning
        case .celebration: .impact(weight: .heavy, intensity: 1)
        }
    }
}
