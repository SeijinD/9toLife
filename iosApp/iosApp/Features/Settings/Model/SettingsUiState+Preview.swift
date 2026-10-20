import SharedLogic

#if DEBUG
extension SettingsUiState {
    static let preview = SettingsUiState(
        themeMode: .system,
        language: .system,
        shiftDurationMinutes: 510,
        reminderOffsetsMinutes: [60, 30, 10].map { KotlinInt(int: $0) },
        hapticsEnabled: true,
        seriousMode: false,
        hasBackgroundImage: true,
        hasQr: true,
    )
}
#endif
