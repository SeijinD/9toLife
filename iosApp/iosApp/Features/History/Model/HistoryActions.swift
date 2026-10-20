import SharedLogic

struct HistoryActions {
    let onPreviousMonth: () -> Void
    let onNextMonth: () -> Void
    let onDaySelect: (Int) -> Void
    let onDeleteShift: (CompletedShift) -> Void
}

#if DEBUG
extension HistoryActions {
    static let preview = HistoryActions(onPreviousMonth: {}, onNextMonth: {}, onDaySelect: { _ in }, onDeleteShift: { _ in })
}
#endif
