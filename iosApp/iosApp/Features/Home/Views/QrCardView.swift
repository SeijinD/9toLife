import SwiftUI

struct QrCardView: View {
    let payload: String
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            QrCodeImage(payload: payload)
                .frame(width: Layout.qrSize, height: Layout.qrSize)
                .padding(Layout.padding)
                .background(.white, in: .rect(cornerRadius: Layout.cornerRadius))
                .shadow(color: .black.opacity(Layout.shadowOpacity), radius: Layout.shadowRadius, y: Layout.shadowY)
        }
        .buttonStyle(.plain)
        .accessibilityLabel("home_qr_cd")
    }
}

private enum Layout {
    static let qrSize: Double = 232
    static let padding: Double = 20
    static let cornerRadius: Double = 28
    static let shadowOpacity: Double = 0.15
    static let shadowRadius: Double = 12
    static let shadowY: Double = 4
}
