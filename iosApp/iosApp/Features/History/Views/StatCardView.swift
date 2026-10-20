import SwiftUI

struct StatCardView: View {
    let value: String
    let label: LocalizedStringKey

    var body: some View {
        VStack(alignment: .leading, spacing: Layout.spacing) {
            Text(verbatim: value)
                .font(.headline)
                .lineLimit(1)
                .minimumScaleFactor(Layout.minimumScale)
            Text(label)
                .font(.caption)
                .foregroundStyle(.secondary)
                .lineLimit(1)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(Layout.padding)
        .background(Color(.appSurface), in: .rect(cornerRadius: Layout.cornerRadius))
    }
}

private enum Layout {
    static let spacing: Double = 2
    static let padding: Double = 12
    static let cornerRadius: Double = 12
    static let minimumScale: Double = 0.7
}
