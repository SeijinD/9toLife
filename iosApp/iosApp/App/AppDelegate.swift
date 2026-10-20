import UIKit

/// SwiftUI has no hook for Home Screen quick actions; they arrive through the scene delegate.
final class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        configurationForConnecting connectingSceneSession: UISceneSession,
        options: UIScene.ConnectionOptions,
    ) -> UISceneConfiguration {
        // A quick action that launches the app arrives here, not in the scene delegate.
        if options.shortcutItem?.type == QuickQrShortcut.type {
            QuickQrInbox.shared.request()
        }
        let configuration = UISceneConfiguration(name: nil, sessionRole: connectingSceneSession.role)
        configuration.delegateClass = SceneDelegate.self
        return configuration
    }
}
