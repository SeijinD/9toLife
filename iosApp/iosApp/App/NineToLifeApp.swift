import SharedLogic
import SwiftUI
import UserNotifications

@main
struct NineToLifeApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) private var appDelegate
    private let dependencies: IosDependencies
    private let shiftAlerts: ShiftAlertsIOS
    private let notificationPresenter = NotificationPresenter()
    // Created here, where init runs once; RootView's init runs on every App body pass.
    @State private var appModel: AppModel

    init() {
        let shiftAlerts = ShiftAlertsIOS()
        IosKoinKt.startKoinIos(shiftAlerts: shiftAlerts)
        self.shiftAlerts = shiftAlerts
        let dependencies = IosDependencies()
        self.dependencies = dependencies
        _appModel = State(initialValue: AppModel(owner: dependencies.appViewModel()))
        UNUserNotificationCenter.current().delegate = notificationPresenter
    }

    var body: some Scene {
        WindowGroup {
            RootView(dependencies: dependencies, shiftAlerts: shiftAlerts, appModel: appModel)
        }
    }
}
