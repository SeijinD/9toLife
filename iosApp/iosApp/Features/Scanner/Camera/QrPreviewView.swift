import AVFoundation
import UIKit

final class QrPreviewView: UIView {
    override class var layerClass: AnyClass { AVCaptureVideoPreviewLayer.self }

    var previewLayer: AVCaptureVideoPreviewLayer {
        guard let layer = layer as? AVCaptureVideoPreviewLayer else {
            preconditionFailure("layerClass is AVCaptureVideoPreviewLayer")
        }
        return layer
    }
}
