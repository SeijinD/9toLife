import AVFoundation
import SwiftUI
import UIKit

/// SwiftUI has no camera view, so AVFoundation is wrapped here.
struct QrCameraView: UIViewRepresentable {
    let onQrDetected: (String) -> Void

    func makeCoordinator() -> QrCaptureCoordinator {
        QrCaptureCoordinator(onQrDetected: onQrDetected)
    }

    func makeUIView(context: Context) -> QrPreviewView {
        let view = QrPreviewView()
        view.previewLayer.session = context.coordinator.session
        view.previewLayer.videoGravity = .resizeAspectFill
        context.coordinator.start()
        return view
    }

    func updateUIView(_ uiView: QrPreviewView, context: Context) {
        context.coordinator.onQrDetected = onQrDetected
    }

    static func dismantleUIView(_ uiView: QrPreviewView, coordinator: QrCaptureCoordinator) {
        coordinator.stop()
    }
}
