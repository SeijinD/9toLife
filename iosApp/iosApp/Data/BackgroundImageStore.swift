import Foundation
import ImageIO
import UniformTypeIdentifiers

/// Copies a picked photo into the app's documents, downscaled and with its orientation applied,
/// so Home never decodes a full-size camera image. Each save gets a new file name so the UI reloads it;
/// old files are left to `deleteAll(except:)`.
struct BackgroundImageStore: Sendable {
    enum StoreError: Error {
        case unreadableImage
        case writeFailed
    }

    private enum Limits {
        static let maxEdgePixels = 2160
        static let jpegQuality: Double = 0.88
    }

    private static var directory: URL { URL.documentsDirectory.appending(path: "backgrounds") }

    /// The app container path changes across installs and updates, so only the file name is stored.
    /// Older values that are full paths still resolve through their last component.
    static func fileURL(forStored value: String) -> URL {
        directory.appending(path: fileName(forStored: value))
    }

    private static func fileName(forStored value: String) -> String {
        URL(fileURLWithPath: value).lastPathComponent
    }

    func save(_ data: Data) async throws -> String {
        let options: [CFString: Any] = [
            kCGImageSourceCreateThumbnailFromImageAlways: true,
            kCGImageSourceCreateThumbnailWithTransform: true,
            kCGImageSourceThumbnailMaxPixelSize: Limits.maxEdgePixels,
        ]
        guard
            let source = CGImageSourceCreateWithData(data as CFData, nil),
            let image = CGImageSourceCreateThumbnailAtIndex(source, 0, options as CFDictionary)
        else { throw StoreError.unreadableImage }

        try FileManager.default.createDirectory(at: Self.directory, withIntermediateDirectories: true)
        let target = Self.directory.appending(path: "background_\(UUID().uuidString).jpg")
        guard let destination = CGImageDestinationCreateWithURL(target as CFURL, UTType.jpeg.identifier as CFString, 1, nil) else {
            throw StoreError.writeFailed
        }
        CGImageDestinationAddImage(destination, image, [kCGImageDestinationLossyCompressionQuality: Limits.jpegQuality] as CFDictionary)
        guard CGImageDestinationFinalize(destination) else { throw StoreError.writeFailed }
        return target.lastPathComponent
    }

    /// Removes every stored photo except the `stored` one. Called with the saved setting, so a photo is
    /// deleted only after the setting stopped pointing to it, whatever happened in between.
    func deleteAll(except stored: String?) async throws {
        guard FileManager.default.fileExists(atPath: Self.directory.path(percentEncoded: false)) else { return }
        let keep = stored.map(Self.fileName(forStored:))
        let files = try FileManager.default.contentsOfDirectory(at: Self.directory, includingPropertiesForKeys: nil)
        for file in files where file.lastPathComponent != keep {
            try FileManager.default.removeItem(at: file)
        }
    }
}
