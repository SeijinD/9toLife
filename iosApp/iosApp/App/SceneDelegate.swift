import UIKit

final class SceneDelegate: NSObject, UIWindowSceneDelegate {
    func windowScene(
        _ windowScene: UIWindowScene,
        performActionFor shortcutItem: UIApplicationShortcutItem,
        completionHandler: @escaping (Bool) -> Void,
    ) {
        guard shortcutItem.type == QuickQrShortcut.type else {
            completionHandler(false)
            return
        }
        QuickQrInbox.shared.request()
        completionHandler(true)
    }
}
