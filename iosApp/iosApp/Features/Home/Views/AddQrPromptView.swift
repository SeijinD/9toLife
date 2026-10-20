import SwiftUI

struct AddQrPromptView: View {
    let seriousMode: Bool
    let onAddQr: () -> Void

    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @State private var isPulsing = false

    var body: some View {
        VStack(spacing: Layout.spacing) {
            Button(action: onAddQr) {
                Image(systemName: "plus")
                    .font(.system(size: Layout.iconSize, weight: .semibold))
                    .foregroundStyle(.white)
                    .frame(width: Layout.buttonSize, height: Layout.buttonSize)
                    .background(Color.accentColor, in: .circle)
                    .background {
                        Circle()
                            .fill(Color.accentColor)
                            .scaleEffect(isPulsing ? Layout.pulseScale : 1)
                            .opacity(isPulsing ? 0 : Layout.pulseOpacity)
                    }
            }
            .buttonStyle(.plain)
            .accessibilityLabel("home_add_qr_cd")
            .padding(.bottom, Layout.buttonBottomPadding)

            Text("home_add_qr_title")
                .font(.title2.weight(.semibold))
                .multilineTextAlignment(.center)
            Text(seriousMode ? "home_add_qr_subtitle_serious" : "home_add_qr_subtitle_fun")
                .font(.body)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
        }
        .padding(.horizontal, Layout.horizontalPadding)
        .onAppear {
            guard !reduceMotion else { return }
            withAnimation(.easeOut(duration: Motion.pulse).repeatForever(autoreverses: false)) {
                isPulsing = true
            }
        }
    }
}

private enum Layout {
    static let buttonSize: Double = 120
    static let iconSize: Double = 52
    static let pulseScale: Double = 1.45
    static let pulseOpacity: Double = 0.35
    static let spacing: Double = 12
    static let buttonBottomPadding: Double = 20
    static let horizontalPadding: Double = 32
}

private enum Motion {
    static let pulse: Double = 1.8
}
