import SharedLogic

#if DEBUG
extension HomeUiState {
    static let preview = HomeUiState(
        isLoading: false,
        qrPayload: "https://example.gov.gr/card/4F7A1C",
        backgroundImagePath: nil,
        isQrExpanded: false,
        shift: ShiftProgress(
            startedAtEpochMillis: 1_760_689_200_000,
            endsAtEpochMillis: 1_760_718_000_000,
            nowEpochMillis: 1_760_700_000_000,
        ),
        seriousMode: false,
        totals: ShiftTotals(shiftCount: 37, overtimeMinutes: 750),
    )
}
#endif
