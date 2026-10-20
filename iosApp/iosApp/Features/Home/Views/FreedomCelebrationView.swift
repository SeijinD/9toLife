import SwiftUI

/// End-of-shift overlay. Dismisses itself when the confetti has fallen, or on tap.
struct FreedomCelebrationView: View {
    let onFinished: () -> Void

    @State private var startDate = Date.now
    @State private var titleScale: Double = Layout.titleStartScale
    @State private var particles = (0..<Layout.particleCount).map { _ in ConfettiParticle.random() }
    @Environment(\.accessibilityReduceMotion) private var reduceMotion

    var body: some View {
        Button(action: onFinished) {
            ZStack {
                Color.black.opacity(Layout.scrimOpacity)
                if !reduceMotion {
                    TimelineView(.animation) { timeline in
                        Canvas { context, size in
                            let progress = min(1, timeline.date.timeIntervalSince(startDate) / Motion.confettiSeconds)
                            for particle in particles {
                                particle.draw(in: &context, size: size, progress: progress)
                            }
                        }
                    }
                }
                VStack(spacing: Layout.textSpacing) {
                    Text("home_freedom_title")
                        .font(.system(.largeTitle, design: .rounded, weight: .black))
                        .foregroundStyle(Color(.brandBrass))
                        .lineLimit(1)
                        .minimumScaleFactor(Layout.titleMinimumScale)
                    Text("home_freedom_subtitle")
                        .font(.headline)
                        .foregroundStyle(.white)
                }
                .multilineTextAlignment(.center)
                .padding(.horizontal, Layout.textPadding)
                .scaleEffect(titleScale)
            }
            .ignoresSafeArea()
        }
        .buttonStyle(.plain)
        .task {
            if reduceMotion {
                titleScale = 1
            } else {
                withAnimation(.spring(duration: Motion.titleSpring, bounce: Motion.titleBounce)) { titleScale = 1 }
            }
            do {
                try await Task.sleep(for: .seconds(Motion.confettiSeconds))
            } catch {
                return
            }
            onFinished()
        }
    }
}

private enum Layout {
    static let particleCount = 90
    static let scrimOpacity: Double = 0.55
    static let titleStartScale: Double = 0.4
    static let titleMinimumScale: Double = 0.4
    static let textSpacing: Double = 8
    static let textPadding: Double = 32
}

private enum Motion {
    static let confettiSeconds: Double = 2.8
    static let titleSpring: Double = 0.6
    static let titleBounce: Double = 0.45
}
