import AVFoundation
import SharedLogic
import SwiftUI

struct ScannerScreen: View {
    let model: ScannerModel
    @State private var permission = CameraPermission.current
    @State private var haptic = HapticTrigger()
    @Environment(\.dismiss) private var dismiss
    @Environment(\.openURL) private var openURL
    @Environment(\.scenePhase) private var scenePhase
    @Environment(\.hapticsEnabled) private var hapticsEnabled

    var body: some View {
        ScannerContent(
            permission: permission,
            actions: ScannerActions(
                onClose: { dismiss() },
                onGrantPermission: requestPermission,
                onOpenSettings: openSettings,
            ),
        ) {
            QrCameraView(onQrDetected: model.onQrScanned)
        }
        .hapticFeedback(haptic, enabled: hapticsEnabled)
        .task {
            if permission == .requestable {
                await requestCameraAccess()
            }
        }
        .task {
            for await event in model.events {
                switch onEnum(of: event) {
                case .qrSaved:
                    haptic.fire(.success)
                    dismiss()
                }
            }
        }
        .onChange(of: scenePhase) { _, phase in
            // Re-checks after a trip to the system settings.
            if phase == .active { permission = .current }
        }
    }

    private func requestPermission() {
        Task { await requestCameraAccess() }
    }

    private func requestCameraAccess() async {
        _ = await AVCaptureDevice.requestAccess(for: .video)
        permission = .current
    }

    private func openSettings() {
        guard let url = URL(string: UIApplication.openSettingsURLString) else { return }
        openURL(url)
    }
}
