import SwiftUI

struct ReminderSectionView: View {
    let offsets: [Int]
    let addableOffsets: [Int]
    let onAdd: (Int) -> Void
    let onRemove: (Int) -> Void

    @Environment(\.locale) private var locale

    var body: some View {
        Section {
            if offsets.isEmpty {
                Text("settings_reminders_empty")
                    .foregroundStyle(.secondary)
            }
            ForEach(offsets, id: \.self) { offset in
                Label(locale.duration(minutes: Int64(offset)), systemImage: "bell")
                    .swipeActions {
                        Button("settings_reminder_remove", systemImage: "trash", role: .destructive) {
                            onRemove(offset)
                        }
                    }
            }
            if !addableOffsets.isEmpty {
                Menu("settings_reminder_add", systemImage: "plus") {
                    ForEach(addableOffsets, id: \.self) { offset in
                        Button(locale.duration(minutes: Int64(offset))) { onAdd(offset) }
                    }
                }
            }
        } header: {
            Text("settings_reminders_label")
        }
    }
}
