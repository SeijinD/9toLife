import AVFoundation

enum CameraPermission: Equatable {
    case granted
    case requestable
    case blocked

    static var current: CameraPermission {
        switch AVCaptureDevice.authorizationStatus(for: .video) {
        case .authorized: .granted
        case .notDetermined: .requestable
        default: .blocked
        }
    }
}
