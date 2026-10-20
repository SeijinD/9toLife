import SwiftUI

struct ConfettiParticle {
    private static let colors: [Color] = [Color(.brandBrass), .red, .blue, .green, .white]
    private static let size = CGSize(width: 9, height: 5)
    private static let maxSpin: Double = 720

    let startX: Double
    let drift: Double
    let speed: Double
    let spin: Double
    let delay: Double
    let color: Color

    static func random() -> ConfettiParticle {
        ConfettiParticle(
            startX: .random(in: 0...1),
            drift: .random(in: -0.5...0.5),
            speed: .random(in: 0.7...1.3),
            spin: .random(in: -maxSpin / 2...maxSpin / 2),
            delay: .random(in: 0...0.3),
            color: colors.randomElement() ?? .white,
        )
    }

    func draw(in context: inout GraphicsContext, size canvas: CGSize, progress: Double) {
        let t = min(1, max(0, (progress - delay) / (1 - delay)))
        guard t > 0 else { return }
        let x = (startX + drift * t) * canvas.width
        let y = -Self.size.height + t * speed * (canvas.height + Self.size.height * 2)
        var particleContext = context
        particleContext.translateBy(x: x, y: y)
        particleContext.rotate(by: .degrees(spin * t))
        particleContext.fill(Path(CGRect(origin: .zero, size: Self.size)), with: .color(color))
    }
}
