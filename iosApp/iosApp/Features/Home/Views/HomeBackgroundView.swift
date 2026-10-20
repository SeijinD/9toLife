import SwiftUI
import UIKit

struct HomeBackgroundView: View {
    let imagePath: String?

    @State private var image: UIImage?

    var body: some View {
        ZStack {
            if let image {
                Image(uiImage: image)
                    .resizable()
                    .scaledToFill()
                    .accessibilityHidden(true)
                Color(.appBackground).opacity(Layout.scrimOpacity)
            } else {
                LinearGradient(
                    colors: [Color(.brandBrass).opacity(Layout.tintOpacity), Color(.appBackground), Color(.appBackground)],
                    startPoint: .top,
                    endPoint: .bottom,
                )
            }
        }
        .ignoresSafeArea()
        .task(id: imagePath) {
            image = await Self.load(path: imagePath)
        }
    }

    /// Off the main actor: decoding a photo is too slow for the UI thread.
    private nonisolated static func load(path: String?) async -> UIImage? {
        guard let path else { return nil }
        let url = BackgroundImageStore.fileURL(forStored: path)
        return UIImage(contentsOfFile: url.path(percentEncoded: false))?.preparingForDisplay()
    }
}

private enum Layout {
    static let scrimOpacity: Double = 0.62
    static let tintOpacity: Double = 0.25
}
