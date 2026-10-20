import Foundation

extension Int64 {
    var epochMillisDate: Date {
        Date(timeIntervalSince1970: Double(self) / 1000)
    }
}

extension Locale {
    func timeOfDay(epochMillis: Int64) -> String {
        epochMillis.epochMillisDate.formatted(Date.FormatStyle(date: .omitted, time: .shortened, locale: self))
    }

    /// "8 hr, 30 min" style, localized.
    func duration(_ duration: Duration) -> String {
        duration.formatted(.units(allowed: [.hours, .minutes], width: .abbreviated).locale(self))
    }

    func duration(minutes: Int64) -> String {
        duration(.seconds(minutes * 60))
    }

    /// "07:59:59" for a running countdown.
    func clock(millis: Int64) -> String {
        Duration.milliseconds(millis).formatted(
            .time(pattern: .hourMinuteSecond(padHourToLength: 2)).locale(self),
        )
    }
}
