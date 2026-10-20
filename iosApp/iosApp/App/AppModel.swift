import Foundation
import Observation
import SharedLogic

@MainActor
@Observable
final class AppModel {
    private(set) var state: AppUiState

    private let owner: IosViewModelOwner<AppViewModel>

    init(owner: IosViewModelOwner<AppViewModel>) {
        self.owner = owner
        state = owner.viewModel.uiState.value
    }

    deinit {
        owner.clear()
    }

    var locale: Locale {
        state.language.languageTag.map { Locale(identifier: $0) } ?? .autoupdatingCurrent
    }

    func observe() async {
        for await state in owner.viewModel.uiState {
            self.state = state
        }
    }
}
