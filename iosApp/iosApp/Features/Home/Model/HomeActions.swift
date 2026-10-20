struct HomeActions {
    let onAddQr: () -> Void
    let onQrTap: () -> Void
    let onQrDismiss: () -> Void
    let onSettings: () -> Void
    let onHistory: () -> Void
    let onUndo: () -> Void
    let onCelebrationFinished: () -> Void
}

#if DEBUG
extension HomeActions {
    static let preview = HomeActions(
        onAddQr: {},
        onQrTap: {},
        onQrDismiss: {},
        onSettings: {},
        onHistory: {},
        onUndo: {},
        onCelebrationFinished: {},
    )
}
#endif
