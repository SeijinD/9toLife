import AppIntents
import SwiftUI
import WidgetKit

/// Control Center and Lock Screen button that opens the app on the QR, which clocks in or out.
struct QuickQrControl: ControlWidget {
    var body: some ControlWidgetConfiguration {
        StaticControlConfiguration(kind: "com.seijind.ninetolife.quickqr") {
            ControlWidgetButton(action: OpenQuickQrIntent()) {
                Label("quick_qr_label", systemImage: "qrcode")
            }
        }
        .displayName("quick_qr_label")
        .description("quick_qr_description")
    }
}
