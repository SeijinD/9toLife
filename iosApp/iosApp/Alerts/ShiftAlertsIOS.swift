import ActivityKit
import Foundation
import OSLog
import SharedLogic
import Synchronization
import UserNotifications

/// iOS side of a running shift: a Live Activity with the live countdown and local notifications
/// for the reminders. Called from shared Kotlin code, which runs it on the main thread.
final class ShiftAlertsIOS: NSObject, ShiftAlerts {
    private enum Identifier {
        /// Every shift notification starts with this, so stale ones can be found after a relaunch.
        static let prefix = "shift."
        static let end = "shift.end"
        static let forgotClockOut = "shift.forgotClockOut"
        static func reminder(_ offset: Int) -> String { "shift.reminder.\(offset)" }
    }

    private let logger = Logger(subsystem: "com.seijind.ninetolife", category: "ShiftAlerts")
    private let locale = Mutex<Locale>(.autoupdatingCurrent)
    /// The last schedule or cancel; the next one waits for it, so they never interleave.
    private let lastWork = Mutex<Task<Void, Never>?>(nil)

    func setLocale(_ locale: Locale) {
        self.locale.withLock { $0 = locale }
    }

    func schedule(shift: ActiveShift, reminderOffsetsMinutes: [KotlinInt], seriousMode: Bool) {
        let offsets = reminderOffsetsMinutes.map(\.intValue)
        let locale = locale.withLock { $0 }
        replaceLastWork { previous in
            Task { @MainActor in
                await previous?.value
                // Delivered ones stay: a relaunch reschedules a running shift and must not wipe its past alerts.
                await Self.removeShiftNotifications(includingDelivered: false)
                guard !Task.isCancelled else { return }
                await self.scheduleNotifications(shift: shift, offsets: offsets, seriousMode: seriousMode, locale: locale)
                guard !Task.isCancelled else { return }
                await self.startLiveActivity(shift: shift, seriousMode: seriousMode, locale: locale)
            }
        }
    }

    func cancel() {
        replaceLastWork { previous in
            Task { @MainActor in
                await previous?.value
                await Self.removeShiftNotifications(includingDelivered: true)
                await Self.endLiveActivities()
            }
        }
    }

    /// Cancels the running schedule (a cancel must win over it) and chains the new work after it.
    private func replaceLastWork(_ makeWork: (Task<Void, Never>?) -> Task<Void, Never>) {
        lastWork.withLock { last in
            last?.cancel()
            last = makeWork(last)
        }
    }

    @MainActor
    private static func removeShiftNotifications(includingDelivered: Bool) async {
        let center = UNUserNotificationCenter.current()
        let pending = await center.pendingNotificationRequests()
            .map(\.identifier)
            .filter { $0.hasPrefix(Identifier.prefix) }
        center.removePendingNotificationRequests(withIdentifiers: pending)
        guard includingDelivered else { return }
        let delivered = await center.deliveredNotifications()
            .map(\.request.identifier)
            .filter { $0.hasPrefix(Identifier.prefix) }
        center.removeDeliveredNotifications(withIdentifiers: delivered)
    }

    /// Notifications survive a relaunch but the Live Activity may not, so a running shift is re-posted at launch.
    @MainActor
    func restore(using restoreShiftAlerts: RestoreShiftAlertsUseCase) async {
        do {
            try await restoreShiftAlerts.invoke()
        } catch {
            logger.error("Restoring shift alerts failed: \(error.localizedDescription, privacy: .public)")
        }
    }

    @MainActor
    private func scheduleNotifications(shift: ActiveShift, offsets: [Int], seriousMode: Bool, locale: Locale) async {
        let center = UNUserNotificationCenter.current()
        do {
            guard try await center.requestAuthorization(options: [.alert, .sound]) else { return }
        } catch {
            logger.error("Notification authorization failed: \(error.localizedDescription, privacy: .public)")
            return
        }
        let bundle = Bundle.localized(for: locale)
        let endsAt = shift.endsAtEpochMillis.epochMillisDate

        for offset in offsets {
            let fireDate = endsAt.addingTimeInterval(-Double(offset) * 60)
            guard fireDate > .now else { continue }
            let left = locale.duration(minutes: Int64(offset))
            let content = UNMutableNotificationContent()
            content.title = seriousMode
                ? String(localized: "notification_reminder_title_serious \(left)", bundle: bundle)
                : String(localized: "notification_reminder_title_fun \(left)", bundle: bundle)
            content.sound = .default
            guard !Task.isCancelled else { return }
            await add(id: Identifier.reminder(offset), content: content, at: fireDate, center: center)
        }

        if endsAt > .now {
            let content = UNMutableNotificationContent()
            content.title = seriousMode
                ? String(localized: "notification_end_title_serious", bundle: bundle)
                : String(localized: "notification_end_title_fun", bundle: bundle)
            content.body = seriousMode
                ? String(localized: "notification_end_text_serious", bundle: bundle)
                : String(localized: "notification_end_text_fun", bundle: bundle)
            content.sound = .default
            await add(id: Identifier.end, content: content, at: endsAt, center: center)
        }

        let forgotAt = endsAt.addingTimeInterval(Double(Timing.forgotClockOutDelay.components.seconds))
        if forgotAt > .now {
            let content = UNMutableNotificationContent()
            content.title = seriousMode
                ? String(localized: "notification_forgot_title_serious", bundle: bundle)
                : String(localized: "notification_forgot_title_fun \(locale.duration(Timing.forgotClockOutDelay))", bundle: bundle)
            content.sound = .default
            await add(id: Identifier.forgotClockOut, content: content, at: forgotAt, center: center)
        }
    }

    @MainActor
    private func add(id: String, content: UNNotificationContent, at date: Date, center: UNUserNotificationCenter) async {
        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: max(1, date.timeIntervalSinceNow), repeats: false)
        do {
            try await center.add(UNNotificationRequest(identifier: id, content: content, trigger: trigger))
        } catch {
            logger.error("Scheduling \(id, privacy: .public) failed: \(error.localizedDescription, privacy: .public)")
        }
    }

    @MainActor
    private func startLiveActivity(shift: ActiveShift, seriousMode: Bool, locale: Locale) async {
        guard ActivityAuthorizationInfo().areActivitiesEnabled else { return }
        let bundle = Bundle.localized(for: locale)
        let endsAt = shift.endsAtEpochMillis.epochMillisDate
        let endTime = locale.timeOfDay(epochMillis: shift.endsAtEpochMillis)
        let startTime = locale.timeOfDay(epochMillis: shift.startedAtEpochMillis)
        let state = ShiftActivityAttributes.ContentState(
            title: seriousMode
                ? String(localized: "notification_running_title_serious \(endTime)", bundle: bundle)
                : String(localized: "notification_running_title_fun \(endTime)", bundle: bundle),
            subtitle: String(localized: "notification_running_text \(startTime)", bundle: bundle),
            overtimeLabel: seriousMode
                ? String(localized: "notification_overtime_title_serious", bundle: bundle)
                : String(localized: "notification_overtime_title_fun", bundle: bundle),
        )
        let attributes = ShiftActivityAttributes(startedAt: shift.startedAtEpochMillis.epochMillisDate, endsAt: endsAt)
        let content = ActivityContent(state: state, staleDate: endsAt)
        // The same shift's activity is updated in place, so a settings change does not make it vanish and reappear.
        let activities = Activity<ShiftActivityAttributes>.activities
        if let current = activities.first(where: { $0.attributes.startedAt == attributes.startedAt && $0.attributes.endsAt == endsAt }) {
            await current.update(content)
            for other in activities where other.id != current.id {
                await other.end(nil, dismissalPolicy: .immediate)
            }
            return
        }
        await Self.endLiveActivities()
        do {
            _ = try Activity.request(attributes: attributes, content: content)
        } catch {
            logger.error("Starting the Live Activity failed: \(error.localizedDescription, privacy: .public)")
        }
    }

    private static func endLiveActivities() async {
        for activity in Activity<ShiftActivityAttributes>.activities {
            await activity.end(nil, dismissalPolicy: .immediate)
        }
    }
}

private enum Timing {
    /// How long after the planned end a still-running shift gets a "forgot to clock out?" nudge.
    static let forgotClockOutDelay: Duration = .seconds(10 * 60)
}
