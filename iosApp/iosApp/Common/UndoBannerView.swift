import SwiftUI

/// Snackbar-style message with an optional undo; SwiftUI has no built-in equivalent.
struct UndoBannerView: View {
    /// How long a banner stays before it hides and its undo expires.
    static let displayDuration: Duration = .seconds(6)

    let message: Text
    let onUndo: (() -> Void)?

    var body: some View {
        HStack(spacing: Layout.spacing) {
            message
                .font(.subheadline)
                .frame(maxWidth: .infinity, alignment: .leading)
            if let onUndo {
                Button("common_undo", action: onUndo)
                    .font(.subheadline.weight(.semibold))
            }
        }
        .padding(Layout.padding)
        .background(.regularMaterial, in: .rect(cornerRadius: Layout.cornerRadius))
        .accessibilityElement(children: .combine)
    }
}

private enum Layout {
    static let spacing: Double = 12
    static let padding: Double = 16
    static let cornerRadius: Double = 16
}

#Preview {
    UndoBannerView(message: Text("history_shift_deleted"), onUndo: {})
        .padding()
}
