import Observation
import OSLog
import PhotosUI
import SharedLogic
import SwiftUI

@MainActor
@Observable
final class SettingsModel {
    private(set) var state: SettingsUiState

    private let owner: IosViewModelOwner<SettingsViewModel>
    private var viewModel: SettingsViewModel { owner.viewModel }
    private let backgroundStore = BackgroundImageStore()
    private let logger = Logger(subsystem: "com.seijind.ninetolife", category: "Settings")

    // Two-way bindings for pickers and toggles; writes still go through the shared ViewModel.
    var themeMode: ThemeMode {
        get { state.themeMode }
        set { viewModel.onThemeModeSelect(mode: newValue) }
    }

    var language: AppLanguage {
        get { state.language }
        set { viewModel.onLanguageSelect(language: newValue) }
    }

    var hapticsEnabled: Bool {
        get { state.hapticsEnabled }
        set { viewModel.onHapticsToggle(enabled: newValue) }
    }

    var seriousMode: Bool {
        get { state.seriousMode }
        set { viewModel.onSeriousModeToggle(enabled: newValue) }
    }

    init(owner: IosViewModelOwner<SettingsViewModel>) {
        self.owner = owner
        state = owner.viewModel.uiState.value
    }

    deinit {
        owner.clear()
    }

    var events: SkieSwiftFlow<SettingsEvent> { viewModel.events }

    func observe() async {
        for await state in viewModel.uiState {
            self.state = state
        }
    }

    func onDurationDecrease() { viewModel.onDurationDecrease() }

    func onDurationIncrease() { viewModel.onDurationIncrease() }

    func onReminderAdd(_ offset: Int) { viewModel.onReminderAdd(offsetMinutes: Int32(offset)) }

    func onReminderRemove(_ offset: Int) { viewModel.onReminderRemove(offsetMinutes: Int32(offset)) }

    /// Copies the picked photo and saves it as the Home background. Returns whether it worked.
    func onBackgroundPicked(_ item: PhotosPickerItem) async -> Bool {
        do {
            guard let data = try await item.loadTransferable(type: Data.self) else { return false }
            let path = try await backgroundStore.save(data)
            viewModel.onBackgroundImagePicked(path: path)
            return true
        } catch is CancellationError {
            return false
        } catch {
            logger.error("Background import failed: \(error.localizedDescription, privacy: .public)")
            return false
        }
    }

    func onBackgroundReset() { viewModel.onBackgroundImageReset() }

    func onChangeQr() { viewModel.onChangeQrClick() }

    func onDeleteQrConfirm() { viewModel.onDeleteQrConfirm() }
}
