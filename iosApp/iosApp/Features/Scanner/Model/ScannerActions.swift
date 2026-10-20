struct ScannerActions {
    let onClose: () -> Void
    let onGrantPermission: () -> Void
    let onOpenSettings: () -> Void
}

#if DEBUG
extension ScannerActions {
    static let preview = ScannerActions(onClose: {}, onGrantPermission: {}, onOpenSettings: {})
}
#endif
