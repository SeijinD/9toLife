import SwiftUI

/// Picks up from the launch screen (same navy, same 288pt logo) and grows the logo a little.
struct SplashView: View {
    let isReady: Bool
    let seriousMode: Bool
    let onFinished: () -> Void

    @State private var logoScale: Double = 1
    @State private var taglineOpacity: Double = 0
    @State private var funTagline: LocalizedStringKey = SplashTaglines.random()

    var body: some View {
        ZStack {
            Color(.brandNavy).ignoresSafeArea()
            Image(.logo)
                .resizable()
                .frame(width: Layout.logoSize, height: Layout.logoSize)
                .scaleEffect(logoScale)
                .accessibilityHidden(true)
            if isReady {
                Text(seriousMode ? "splash_tagline_serious" : funTagline)
                    .font(.headline)
                    .foregroundStyle(Color(.brandBrass))
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, Layout.taglinePadding)
                    .offset(y: Layout.taglineOffset)
                    .opacity(taglineOpacity)
            }
        }
        .task(id: isReady) {
            guard isReady else { return }
            withAnimation(.easeOut(duration: Motion.logoScale)) { logoScale = Layout.logoTargetScale }
            withAnimation(.easeIn(duration: Motion.taglineFade).delay(Motion.taglineDelay)) { taglineOpacity = 1 }
            do {
                try await Task.sleep(for: Motion.splashDuration)
            } catch {
                return
            }
            onFinished()
        }
    }
}

private enum SplashTaglines {
    static let keys: [LocalizedStringKey] = [
        "splash_tagline_fun_1", "splash_tagline_fun_2", "splash_tagline_fun_3", "splash_tagline_fun_4",
        "splash_tagline_fun_5", "splash_tagline_fun_6", "splash_tagline_fun_7",
    ]

    static func random() -> LocalizedStringKey {
        keys.randomElement() ?? "splash_tagline_serious"
    }
}

private enum Layout {
    static let logoSize: Double = 288
    static let logoTargetScale: Double = 1.12
    static let taglineOffset: Double = 120
    static let taglinePadding: Double = 32
}

private enum Motion {
    static let logoScale: Double = 0.65
    static let taglineFade: Double = 0.4
    static let taglineDelay: Double = 0.15
    static let splashDuration: Duration = .seconds(2)
}

#Preview("Default") {
    SplashView(isReady: true, seriousMode: false, onFinished: {})
}

#Preview("Greek, large text") {
    SplashView(isReady: true, seriousMode: false, onFinished: {})
        .environment(\.locale, Locale(identifier: "el"))
        .dynamicTypeSize(.accessibility2)
}
