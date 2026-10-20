import SwiftUI

/// Rows here live in a ScrollView, where `swipeActions` is not available (it needs a List).
/// Swiping left reveals a delete button; a long press offers the same action.
struct SwipeToDeleteRow<Content: View>: View {
    let onDelete: () -> Void
    @ViewBuilder let content: () -> Content

    @State private var offset: Double = 0

    var body: some View {
        ZStack(alignment: .trailing) {
            if offset < 0 {
                Button("history_delete_shift", systemImage: "trash", role: .destructive, action: delete)
                    .labelStyle(.iconOnly)
                    .font(.title3)
                    .foregroundStyle(.white)
                    .frame(width: Layout.buttonWidth)
                    .frame(maxHeight: .infinity)
                    .background(.red, in: .rect(cornerRadius: Layout.cornerRadius))
            }
            content()
                .offset(x: offset)
        }
        .simultaneousGesture(swipe)
        .contextMenu {
            Button("history_delete_shift", systemImage: "trash", role: .destructive, action: onDelete)
        }
        .animation(.snappy, value: offset)
    }

    private var swipe: some Gesture {
        DragGesture(minimumDistance: Layout.minimumDragDistance)
            .onChanged { value in
                // Leave vertical drags to the ScrollView.
                guard abs(value.translation.width) > abs(value.translation.height) else { return }
                offset = min(0, max(value.translation.width, -Layout.buttonWidth * Layout.overshoot))
            }
            .onEnded { _ in
                offset = offset < -Layout.buttonWidth / 2 ? -Layout.buttonWidth : 0
            }
    }

    private func delete() {
        offset = 0
        onDelete()
    }
}

private enum Layout {
    static let buttonWidth: Double = 72
    static let overshoot: Double = 1.5
    static let minimumDragDistance: Double = 20
    static let cornerRadius: Double = 12
}
