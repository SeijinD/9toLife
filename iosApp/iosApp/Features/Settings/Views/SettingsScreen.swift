import PhotosUI
import SharedLogic
import SwiftUI

struct SettingsScreen: View {
    @Bindable var model: SettingsModel
    @State private var photoItem: PhotosPickerItem?
    @State private var isConfirmingDelete = false
    @State private var haptic = HapticTrigger()
    @Environment(\.hapticsEnabled) private var hapticsEnabled

    private let onOpenScanner: () -> Void

    init(model: SettingsModel, onOpenScanner: @escaping () -> Void) {
        self.model = model
        self.onOpenScanner = onOpenScanner
    }

    var body: some View {
        SettingsContent(
            state: model.state,
            actions: SettingsActions(
                onDurationDecrease: model.onDurationDecrease,
                onDurationIncrease: model.onDurationIncrease,
                onReminderAdd: model.onReminderAdd,
                onReminderRemove: model.onReminderRemove,
                onResetBackground: model.onBackgroundReset,
                onChangeQr: model.onChangeQr,
                onDeleteQr: { isConfirmingDelete = true },
            ),
            themeMode: $model.themeMode,
            language: $model.language,
            hapticsEnabled: $model.hapticsEnabled,
            seriousMode: $model.seriousMode,
            photoItem: $photoItem,
        )
        .hapticFeedback(haptic, enabled: hapticsEnabled)
        .confirmationDialog("settings_qr_delete_title", isPresented: $isConfirmingDelete, titleVisibility: .visible) {
            Button("settings_qr_delete_confirm", role: .destructive, action: model.onDeleteQrConfirm)
        } message: {
            Text("settings_qr_delete_body")
        }
        .task { await model.observe() }
        .task {
            for await event in model.events {
                switch onEnum(of: event) {
                case .openScanner: onOpenScanner()
                }
            }
        }
        // Keyed by the item, so a second pick cancels the first.
        .task(id: photoItem) {
            guard let item = photoItem else { return }
            let saved = await model.onBackgroundPicked(item)
            guard !Task.isCancelled else { return }
            haptic.fire(saved ? .success : .warning)
            photoItem = nil
        }
        .onChange(of: model.state.shiftDurationMinutes) {
            haptic.fire(.selection)
        }
    }
}
