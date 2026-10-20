import SwiftUI

struct PermissionPromptView: View {
    let permission: CameraPermission
    let actions: ScannerActions

    var body: some View {
        VStack(spacing: Layout.spacing) {
            Text("scanner_permission_title")
                .font(.title2.weight(.semibold))
            Text("scanner_permission_body")
                .font(.body)
            if permission == .blocked {
                Button("scanner_permission_settings", action: actions.onOpenSettings)
                    .buttonStyle(.borderedProminent)
            } else {
                Button("scanner_permission_grant", action: actions.onGrantPermission)
                    .buttonStyle(.borderedProminent)
            }
        }
        .foregroundStyle(.white)
        .multilineTextAlignment(.center)
        .padding(Layout.padding)
    }
}

private enum Layout {
    static let spacing: Double = 16
    static let padding: Double = 24
}
