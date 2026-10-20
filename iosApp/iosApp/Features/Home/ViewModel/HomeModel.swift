import Observation
import OSLog
import SharedLogic

@MainActor
@Observable
final class HomeModel {
    private(set) var state: HomeUiState

    private let owner: IosViewModelOwner<HomeViewModel>
    private var viewModel: HomeViewModel { owner.viewModel }
    private let backgroundStore = BackgroundImageStore()
    private let logger = Logger(subsystem: "com.seijind.ninetolife", category: "Home")

    init(owner: IosViewModelOwner<HomeViewModel>) {
        self.owner = owner
        state = owner.viewModel.uiState.value
    }

    deinit {
        owner.clear()
    }

    var events: SkieSwiftFlow<HomeEvent> { viewModel.events }

    func observe() async {
        for await state in viewModel.uiState {
            self.state = state
        }
    }

    /// Old background photos go only once the saved setting no longer points to them.
    func deleteUnusedBackgrounds() async {
        guard !state.isLoading else { return }
        do {
            try await backgroundStore.deleteAll(except: state.backgroundImagePath)
        } catch {
            logger.error("Background cleanup failed: \(error.localizedDescription, privacy: .public)")
        }
    }

    func onAddQr() { viewModel.onAddQrClick() }

    func onQrTap() { viewModel.onQrClick() }

    func onQuickQrRequest() { viewModel.onQuickQrRequest() }

    func onQrDismiss() { viewModel.onQrDismiss() }

    func onUndo() { viewModel.onUndoClick() }

    func onUndoExpired() { viewModel.onUndoExpired() }
}
