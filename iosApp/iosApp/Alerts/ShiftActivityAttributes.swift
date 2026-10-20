import ActivityKit
import Foundation

/// Shared by the app and the ShiftWidget extension.
/// Texts arrive already localized because the widget cannot see the in-app language.
/// The activity goes stale at `endsAt`, which is how the widget knows overtime started.
struct ShiftActivityAttributes: ActivityAttributes {
    struct ContentState: Codable, Hashable {
        let title: String
        let subtitle: String
        let overtimeLabel: String
    }

    let startedAt: Date
    let endsAt: Date
}
