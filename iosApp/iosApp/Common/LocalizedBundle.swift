import Foundation

extension Bundle {
    /// The `.lproj` bundle for an in-app language, for strings built outside SwiftUI `Text`
    /// (notifications, Live Activity), which do not follow the `\.locale` environment.
    static func localized(for locale: Locale) -> Bundle {
        guard
            let code = locale.language.languageCode?.identifier,
            let path = Bundle.main.path(forResource: code, ofType: "lproj"),
            let bundle = Bundle(path: path)
        else { return .main }
        return bundle
    }
}
