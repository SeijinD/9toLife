import SwiftUI

/// Changes on every `fire`, so the same kind of feedback can play twice in a row.
struct HapticTrigger: Equatable {
    private(set) var kind: HapticKind?
    private var count = 0

    mutating func fire(_ kind: HapticKind) {
        self.kind = kind
        count += 1
    }
}

extension View {
    func hapticFeedback(_ trigger: HapticTrigger, enabled: Bool) -> some View {
        sensoryFeedback(trigger: trigger) { _, new in
            guard enabled else { return nil }
            return new.kind?.feedback
        }
    }
}
