import SwiftUI

/// `camera` is a slot so the screen stays previewable without a camera.
struct ScannerContent<Camera: View>: View {
    let permission: CameraPermission
    let actions: ScannerActions
    @ViewBuilder let camera: () -> Camera

    var body: some View {
        ZStack {
            Color.black.ignoresSafeArea()
            switch permission {
            case .granted:
                camera().ignoresSafeArea()
                RoundedRectangle(cornerRadius: Layout.viewfinderCorner)
                    .stroke(Color(.brandBrass), lineWidth: Layout.viewfinderStroke)
                    .frame(width: Layout.viewfinderSize, height: Layout.viewfinderSize)
                    .accessibilityHidden(true)
                VStack {
                    Spacer()
                    Text("scanner_hint")
                        .font(.headline)
                        .foregroundStyle(.white)
                        .multilineTextAlignment(.center)
                        .padding(.bottom, Layout.hintBottomPadding)
                }
                .padding(.horizontal)
            case .requestable, .blocked:
                PermissionPromptView(permission: permission, actions: actions)
            }
        }
        .overlay(alignment: .topLeading) {
            Button("common_close", systemImage: "xmark", action: actions.onClose)
                .labelStyle(.iconOnly)
                .font(.title3.weight(.semibold))
                .foregroundStyle(.white)
                .padding(Layout.closePadding)
                .background(.black.opacity(Layout.closeScrimOpacity), in: .circle)
                .padding()
        }
    }
}

private enum Layout {
    static let viewfinderSize: Double = 260
    static let viewfinderStroke: Double = 4
    static let viewfinderCorner: Double = 28
    static let hintBottomPadding: Double = 48
    static let closePadding: Double = 10
    static let closeScrimOpacity: Double = 0.45
}

#Preview("Default") {
    ScannerContent(permission: .requestable, actions: .preview) { Color.gray }
}

#Preview("Greek, large text") {
    ScannerContent(permission: .blocked, actions: .preview) { Color.gray }
        .environment(\.locale, Locale(identifier: "el"))
        .dynamicTypeSize(.accessibility2)
}
