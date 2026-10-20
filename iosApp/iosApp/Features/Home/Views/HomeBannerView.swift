import SwiftUI

struct HomeBannerView: View {
    let banner: HomeBanner
    let seriousMode: Bool
    let onUndo: () -> Void

    @Environment(\.locale) private var locale

    var body: some View {
        UndoBannerView(message: message, onUndo: banner.offersUndo ? onUndo : nil)
    }

    private var message: Text {
        switch banner.kind {
        case .shiftStarted:
            return Text(seriousMode ? "home_shift_started_serious" : "home_shift_started_fun")
        case .shiftEnded(let workedMinutes):
            let worked = locale.duration(minutes: workedMinutes)
            return seriousMode ? Text("home_shift_ended_serious \(worked)") : Text("home_shift_ended_fun \(worked)")
        case .undoDone:
            return Text("home_undo_done")
        case .shiftTimeCompleted:
            return Text("home_freedom_serious")
        }
    }
}
