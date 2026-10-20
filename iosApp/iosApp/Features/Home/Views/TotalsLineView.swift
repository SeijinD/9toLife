import SharedLogic
import SwiftUI

struct TotalsLineView: View {
    let totals: ShiftTotals
    let seriousMode: Bool

    @Environment(\.locale) private var locale

    var body: some View {
        if totals.shiftCount > 0 {
            Group {
                if totals.overtimeMinutes > 0 {
                    Text("home_totals_line \(countText) \(overtimeText)")
                } else {
                    countText
                }
            }
            .font(.footnote.weight(.medium))
            .foregroundStyle(.secondary)
            .multilineTextAlignment(.center)
            .padding(.horizontal)
        }
    }

    private var countText: Text {
        let count = Int(totals.shiftCount)
        return seriousMode ? Text("home_totals_shifts \(count)") : Text("home_totals_galleys \(count)")
    }

    private var overtimeText: Text {
        let overtime = locale.duration(minutes: totals.overtimeMinutes)
        return seriousMode ? Text("home_totals_overtime_serious \(overtime)") : Text("home_totals_overtime_fun \(overtime)")
    }
}
