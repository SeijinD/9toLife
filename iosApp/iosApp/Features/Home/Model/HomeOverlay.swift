/// Home's UI-only presentation state, owned by HomeScreen and passed to HomeContent.
struct HomeOverlay: Equatable {
    var banner: HomeBanner?
    var isCelebrating = false
}
