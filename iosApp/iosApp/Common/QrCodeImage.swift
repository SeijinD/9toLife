import CoreImage
import CoreImage.CIFilterBuiltins
import SwiftUI

/// Renders a QR payload as crisp black modules. Drawn on a white card, whatever the theme,
/// because scanners need dark-on-light.
struct QrCodeImage: View {
    let payload: String

    var body: some View {
        if let image = QrCodeRenderer.image(for: payload) {
            Image(decorative: image, scale: 1)
                .interpolation(.none)
                .resizable()
                .scaledToFit()
        } else {
            Color.clear
        }
    }
}

@MainActor
private enum QrCodeRenderer {
    private static let context = CIContext()
    private static let correctionLevel = "M"
    private static let scale: Double = 10

    static func image(for payload: String) -> CGImage? {
        let filter = CIFilter.qrCodeGenerator()
        filter.message = Data(payload.utf8)
        filter.correctionLevel = correctionLevel
        guard let output = filter.outputImage?.transformed(by: CGAffineTransform(scaleX: scale, y: scale)) else { return nil }
        return context.createCGImage(output, from: output.extent)
    }
}
