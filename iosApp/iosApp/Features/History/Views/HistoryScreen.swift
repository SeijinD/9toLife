import SharedLogic
import SwiftUI

struct HistoryScreen: View {
    let model: HistoryModel
    @State private var haptic = HapticTrigger()
    /// The shift the banner can bring back; nil hides the banner.
    @State private var deletedShift: CompletedShift?
    @Environment(\.hapticsEnabled) private var hapticsEnabled

    var body: some View {
        HistoryContent(
            state: model.state,
            actions: HistoryActions(
                onPreviousMonth: model.onPreviousMonth,
                onNextMonth: model.onNextMonth,
                onDaySelect: model.onDaySelect,
                onDeleteShift: model.onDeleteShift,
            ),
        )
        .overlay(alignment: .bottom) {
            if deletedShift != nil {
                UndoBannerView(message: Text("history_shift_deleted"), onUndo: undoDelete)
                    .padding()
                    .transition(.move(edge: .bottom).combined(with: .opacity))
            }
        }
        .animation(.snappy, value: deletedShift?.id)
        .hapticFeedback(haptic, enabled: hapticsEnabled)
        .onChange(of: model.state.selectedDay) {
            haptic.fire(.selection)
        }
        .task { await model.observe() }
        .task {
            for await event in model.events {
                switch onEnum(of: event) {
                case .shiftDeleted(let deleted):
                    haptic.fire(.warning)
                    deletedShift = deleted.shift
                }
            }
        }
        // Keyed by the shift, so a newer delete restarts the timer.
        .task(id: deletedShift?.id) {
            guard deletedShift != nil else { return }
            do {
                try await Task.sleep(for: UndoBannerView.displayDuration)
            } catch {
                return
            }
            deletedShift = nil
        }
    }

    private func undoDelete() {
        guard let shift = deletedShift else { return }
        deletedShift = nil
        model.onUndoDelete(shift)
    }
}
