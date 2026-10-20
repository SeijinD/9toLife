struct SettingsActions {
    let onDurationDecrease: () -> Void
    let onDurationIncrease: () -> Void
    let onReminderAdd: (Int) -> Void
    let onReminderRemove: (Int) -> Void
    let onResetBackground: () -> Void
    let onChangeQr: () -> Void
    let onDeleteQr: () -> Void
}

#if DEBUG
extension SettingsActions {
    static let preview = SettingsActions(
        onDurationDecrease: {},
        onDurationIncrease: {},
        onReminderAdd: { _ in },
        onReminderRemove: { _ in },
        onResetBackground: {},
        onChangeQr: {},
        onDeleteQr: {},
    )
}
#endif
