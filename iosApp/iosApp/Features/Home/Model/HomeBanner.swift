import Foundation

/// The snackbar-like message at the bottom of Home. Only shift changes offer undo.
struct HomeBanner: Equatable, Identifiable {
    enum Kind: Equatable {
        case shiftStarted
        case shiftEnded(workedMinutes: Int64)
        case undoDone
        case shiftTimeCompleted
    }

    let id = UUID()
    let kind: Kind

    var offersUndo: Bool {
        switch kind {
        case .shiftStarted, .shiftEnded: true
        case .undoDone, .shiftTimeCompleted: false
        }
    }
}
