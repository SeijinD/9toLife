import SwiftUI

/// Full-screen QR for the reader at the door: white field, max brightness, no status bar.
struct ExpandedQrView: View {
    let payload: String
    let onDismiss: () -> Void

    var body: some View {
        Button(action: onDismiss) {
            VStack(spacing: Layout.spacing) {
                Spacer()
                QrCodeImage(payload: payload)
                    .frame(maxWidth: Layout.qrMaxWidth)
                    .accessibilityLabel("home_qr_cd")
                Text("home_qr_close_hint")
                    .font(.subheadline)
                    .foregroundStyle(.black.opacity(Layout.hintOpacity))
                Spacer()
            }
            .padding(Layout.padding)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(.white)
            .contentShape(.rect)
        }
        .buttonStyle(.plain)
        .ignoresSafeArea()
        .statusBarHidden()
        .onAppear(perform: ScreenBrightness.boost)
        .onDisappear(perform: ScreenBrightness.restore)
    }
}

private enum Layout {
    static let qrMaxWidth: Double = 520
    static let padding: Double = 24
    static let spacing: Double = 24
    static let hintOpacity: Double = 0.6
}
