import PhotosUI
import SharedLogic
import SwiftUI

struct SettingsContent: View {
    let state: SettingsUiState
    let actions: SettingsActions
    @Binding var themeMode: ThemeMode
    @Binding var language: AppLanguage
    @Binding var hapticsEnabled: Bool
    @Binding var seriousMode: Bool
    @Binding var photoItem: PhotosPickerItem?

    @Environment(\.locale) private var locale

    var body: some View {
        NavigationStack {
            Form {
                Section {
                    VStack(alignment: .leading, spacing: Layout.durationSpacing) {
                        Text("settings_duration_label")
                        HStack(spacing: Layout.stepperSpacing) {
                            Button("settings_duration_decrease_cd", systemImage: "minus.circle.fill", action: actions.onDurationDecrease)
                                .disabled(!state.canDecreaseDuration)
                            Text(verbatim: durationText)
                                .font(.largeTitle.weight(.semibold))
                                .monospacedDigit()
                                .frame(minWidth: Layout.durationValueWidth)
                            Button("settings_duration_increase_cd", systemImage: "plus.circle.fill", action: actions.onDurationIncrease)
                                .disabled(!state.canIncreaseDuration)
                        }
                        .frame(maxWidth: .infinity)
                        .labelStyle(.iconOnly)
                        .font(.largeTitle)
                        // Borderless, so each button gets its own tap inside the Form row.
                        .buttonStyle(.borderless)
                        .buttonRepeatBehavior(.enabled)
                    }
                    .padding(.vertical, Layout.durationVerticalPadding)
                } header: {
                    Text("settings_section_shift")
                } footer: {
                    Text("settings_duration_next_shift")
                }

                ReminderSectionView(
                    offsets: state.reminderOffsetsMinutes.map(\.intValue),
                    addableOffsets: state.addableReminderOffsetsMinutes.map(\.intValue),
                    onAdd: actions.onReminderAdd,
                    onRemove: actions.onReminderRemove,
                )

                Section {
                    Picker("settings_theme_label", selection: $themeMode) {
                        Text("settings_theme_system").tag(ThemeMode.system)
                        Text("settings_theme_light").tag(ThemeMode.light)
                        Text("settings_theme_dark").tag(ThemeMode.dark)
                    }
                    Picker("settings_language_label", selection: $language) {
                        Text("settings_language_system").tag(AppLanguage.system)
                        Text(verbatim: "Ελληνικά").tag(AppLanguage.greek)
                        Text(verbatim: "English").tag(AppLanguage.english)
                    }
                    PhotosPicker(selection: $photoItem, matching: .images) {
                        Label("settings_background_pick", systemImage: "photo")
                    }
                    if state.hasBackgroundImage {
                        Button("settings_background_reset", systemImage: "arrow.uturn.backward", action: actions.onResetBackground)
                    }
                } header: {
                    Text("settings_section_appearance")
                }

                Section {
                    Toggle("settings_haptics_label", isOn: $hapticsEnabled)
                    Toggle(isOn: $seriousMode) {
                        Text("settings_serious_label")
                        Text("settings_serious_description")
                    }
                } header: {
                    Text("settings_section_experience")
                }

                if state.hasQr {
                    Section {
                        Button("settings_qr_change", systemImage: "qrcode.viewfinder", action: actions.onChangeQr)
                        Button("settings_qr_delete", systemImage: "trash", role: .destructive, action: actions.onDeleteQr)
                    } header: {
                        Text("settings_section_qr")
                    }
                }
            }
            .navigationTitle("settings_title")
            .navigationBarTitleDisplayMode(.inline)
        }
    }

    private var durationText: String {
        Duration.seconds(Int(state.shiftDurationMinutes) * 60).formatted(.time(pattern: .hourMinute).locale(locale))
    }
}

private enum Layout {
    static let stepperSpacing: Double = 12
    static let durationSpacing: Double = 12
    static let durationValueWidth: Double = 120
    static let durationVerticalPadding: Double = 4
}

#Preview("Default") {
    SettingsContent(
        state: .preview,
        actions: .preview,
        themeMode: .constant(.system),
        language: .constant(.system),
        hapticsEnabled: .constant(true),
        seriousMode: .constant(false),
        photoItem: .constant(nil),
    )
}

#Preview("Greek, large text") {
    SettingsContent(
        state: .preview,
        actions: .preview,
        themeMode: .constant(.system),
        language: .constant(.system),
        hapticsEnabled: .constant(true),
        seriousMode: .constant(false),
        photoItem: .constant(nil),
    )
        .environment(\.locale, Locale(identifier: "el"))
        .dynamicTypeSize(.accessibility2)
}
