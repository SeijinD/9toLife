import Foundation
import SharedLogic

/// The in-app language switch updates SwiftUI at once through `\.locale`. Writing `AppleLanguages`
/// also moves what only reads it at launch (system alerts, Info.plist strings) on the next start.
enum AppLanguagePreference {
    private static let appleLanguagesKey = "AppleLanguages"

    @MainActor
    static func apply(language: AppLanguage, shiftAlerts: ShiftAlertsIOS) {
        if let tag = language.languageTag {
            UserDefaults.standard.set([tag], forKey: appleLanguagesKey)
            shiftAlerts.setLocale(Locale(identifier: tag))
        } else {
            UserDefaults.standard.removeObject(forKey: appleLanguagesKey)
            shiftAlerts.setLocale(.autoupdatingCurrent)
        }
    }
}
