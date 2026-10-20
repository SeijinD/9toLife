import SharedLogic

extension AppLanguage {
    /// BCP 47 tag of an explicit choice; nil follows the system.
    var languageTag: String? {
        switch self {
        case .system: nil
        case .greek: "el"
        case .english: "en"
        }
    }
}
