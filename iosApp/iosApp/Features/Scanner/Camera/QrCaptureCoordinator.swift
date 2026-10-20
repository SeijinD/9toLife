import AVFoundation
import OSLog

/// Owns the capture session and reports every QR it reads; the shared ViewModel ignores repeats.
final class QrCaptureCoordinator: NSObject, AVCaptureMetadataOutputObjectsDelegate {
    let session = AVCaptureSession()
    var onQrDetected: (String) -> Void

    private let logger = Logger(subsystem: "com.seijind.ninetolife", category: "Scanner")
    private var isConfigured = false
    /// The last start or stop; the next one waits for it, so a quick dismiss never stops before it started.
    private var lastSessionWork: Task<Void, Never>?

    init(onQrDetected: @escaping (String) -> Void) {
        self.onQrDetected = onQrDetected
    }

    func start() {
        if !isConfigured { configure() }
        let session = session
        let previous = lastSessionWork
        // startRunning blocks until the camera is up; Apple requires calling it off the main thread.
        lastSessionWork = Task.detached(priority: .userInitiated) {
            await previous?.value
            session.startRunning()
        }
    }

    func stop() {
        let session = session
        let previous = lastSessionWork
        lastSessionWork = Task.detached(priority: .userInitiated) {
            await previous?.value
            session.stopRunning()
        }
    }

    func metadataOutput(
        _ output: AVCaptureMetadataOutput,
        didOutput metadataObjects: [AVMetadataObject],
        from connection: AVCaptureConnection,
    ) {
        let payload = metadataObjects
            .compactMap { $0 as? AVMetadataMachineReadableCodeObject }
            .first { $0.type == .qr }?
            .stringValue
        if let payload { onQrDetected(payload) }
    }

    private func configure() {
        guard let device = AVCaptureDevice.default(for: .video) else {
            logger.error("No camera available")
            return
        }
        let input: AVCaptureDeviceInput
        do {
            input = try AVCaptureDeviceInput(device: device)
        } catch {
            logger.error("Camera input failed: \(error.localizedDescription, privacy: .public)")
            return
        }
        guard session.canAddInput(input) else {
            logger.error("Cannot add the camera input")
            return
        }
        let output = AVCaptureMetadataOutput()
        guard session.canAddOutput(output) else {
            logger.error("Cannot add the metadata output")
            return
        }
        session.beginConfiguration()
        session.addInput(input)
        session.addOutput(output)
        // The delegate API takes a queue; main keeps the callback on the UI thread.
        output.setMetadataObjectsDelegate(self, queue: .main)
        output.metadataObjectTypes = [.qr]
        session.commitConfiguration()
        isConfigured = true
    }
}
