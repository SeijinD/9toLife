import Observation
import SharedLogic

@MainActor
@Observable
final class HistoryModel {
    private(set) var state: HistoryUiState

    private let owner: IosViewModelOwner<HistoryViewModel>
    private var viewModel: HistoryViewModel { owner.viewModel }

    init(owner: IosViewModelOwner<HistoryViewModel>) {
        self.owner = owner
        state = owner.viewModel.uiState.value
    }

    deinit {
        owner.clear()
    }

    var events: SkieSwiftFlow<HistoryEvent> { viewModel.events }

    func observe() async {
        for await state in viewModel.uiState {
            self.state = state
        }
    }

    func onPreviousMonth() { viewModel.onPreviousMonthClick() }

    func onNextMonth() { viewModel.onNextMonthClick() }

    func onDaySelect(_ day: Int) { viewModel.onDayClick(dayOfMonth: Int32(day)) }

    func onDeleteShift(_ shift: CompletedShift) { viewModel.onDeleteShift(shift: shift) }

    func onUndoDelete(_ shift: CompletedShift) { viewModel.onUndoDelete(shift: shift) }
}
