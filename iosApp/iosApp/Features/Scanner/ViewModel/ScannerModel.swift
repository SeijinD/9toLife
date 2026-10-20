import SharedLogic

@MainActor
final class ScannerModel {
    private let owner: IosViewModelOwner<ScannerViewModel>

    init(owner: IosViewModelOwner<ScannerViewModel>) {
        self.owner = owner
    }

    deinit {
        owner.clear()
    }

    var events: SkieSwiftFlow<ScannerEvent> { owner.viewModel.events }

    func onQrScanned(_ payload: String) {
        owner.viewModel.onQrScanned(payload: payload)
    }
}
