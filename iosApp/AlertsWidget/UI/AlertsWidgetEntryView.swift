//
//  AlertsWidgetEntryView.swift
//  iosApp
//
//  Created by Jason Bromfield on 2026-09-20.
//  Copyright © 2026 orgName. All rights reserved.
//
import SwiftUI
import coreKit
import WidgetKit
import ComposeApp

struct AlertsWidgetEntryView: View {
    let entry: Provider.Entry
    @Environment(\.widgetFamily) var widgetFamily: WidgetFamily
    @Environment(\.locale) var locale

    var body: some View {
        let languageCode = locale.language.languageCode?.identifier ?? "en"

        let rowCount =
            switch widgetFamily {
            case .systemSmall: 1
            case .systemMedium: 2
            case .systemLarge, .systemExtraLarge: 4
            case .systemExtraLargePortrait: 8
            default: 1
            }
        VStack(alignment: .leading, spacing: 0) {
            ForEach(entry.alerts.prefix(rowCount), id: \.self.id) { alert in
                let affectedLines =
                    alert.affectedLines.joined(separator: ", ")
                    + (alert.affectedLines.isEmpty ? "" : ": ")
                let affectedStops = alert.affectedStops.joined(separator: ", ")
                Link(
                    destination: URL(string: "\(AppKt.ALERTS_URL)/\(alert.id)")!
                ) {
                    VStack(alignment: .leading) {
                        if !affectedStops.isEmpty {
                            Text(affectedStops)
                                .font(.footnote)
                                .bold()
                        }
                        Text(
                            "\(affectedLines)\(alert.getSubject(language: languageCode))"
                        )
                        .font(.footnote)
                        .bold()
                        .lineLimit(2)
                        Text(
                            alert.getAnnotatedBody(language: languageCode, linkColor: Color.accentColor.toRGBInt() ?? 0)
                                .text.trimmingCharacters(in: .whitespaces)
                        )
                        .font(.caption)
                    }.frame(
                        maxWidth: .infinity,
                        minHeight: 40,
                        maxHeight: 200,
                        alignment: .topLeading
                    )
                    .padding(.vertical, 4)
                }
            }
        }.frame(
            maxWidth: .infinity,
            maxHeight: .infinity,
            alignment: .topLeading
        )
    }
}

extension Color {
    /// Returns RGB packed as 0xRRGGBB
    func toRGBInt() -> UInt64? {
        let uiColor = UIColor(self)
        var r: CGFloat = 0, g: CGFloat = 0, b: CGFloat = 0, a: CGFloat = 0
        guard uiColor.getRed(&r, green: &g, blue: &b, alpha: &a) else {
            return nil
        }

        let ri = UInt64(lround(r * 255))
        let gi = UInt64(lround(g * 255))
        let bi = UInt64(lround(b * 255))

        return (ri << 16) | (gi << 8) | bi
    }

    /// Returns RGBA packed as 0xRRGGBBAA
    func toRGBAInt() -> UInt64? {
        let uiColor = UIColor(self)
        var r: CGFloat = 0, g: CGFloat = 0, b: CGFloat = 0, a: CGFloat = 0
        guard uiColor.getRed(&r, green: &g, blue: &b, alpha: &a) else {
            return nil
        }

        let ri = UInt64(lround(r * 255))
        let gi = UInt64(lround(g * 255))
        let bi = UInt64(lround(b * 255))
        let ai = UInt64(lround(a * 255))

        return (ri << 24) | (gi << 16) | (bi << 8) | ai
    }
}
