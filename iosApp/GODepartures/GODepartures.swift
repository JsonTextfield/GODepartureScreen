//
//  GODepartures.swift
//  GODepartures
//
//  Created by Jason Bromfield on 2025-10-13.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import WidgetKit
import coreKit

struct Provider: AppIntentTimelineProvider {

    func placeholder(in context: Context) -> SimpleEntry {
        SimpleEntry(date: Date(), configuration: ConfigurationAppIntent())
    }

    func snapshot(
        for configuration: ConfigurationAppIntent,
        in context: Context
    ) async -> SimpleEntry {
        SimpleEntry(date: Date(), configuration: configuration)
    }

    func timeline(
        for configuration: ConfigurationAppIntent,
        in context: Context
    ) async -> Timeline<SimpleEntry> {
        var entries: [SimpleEntry] = []

        // Generate a timeline consisting of five entries an hour apart, starting from the current date.
        let currentDate = Date()
        for hourOffset in 0..<5 {
            let entryDate = Calendar.current.date(
                byAdding: .hour,
                value: hourOffset,
                to: currentDate
            )!
            let entry = SimpleEntry(
                date: entryDate,
                configuration: configuration
            )
            entries.append(entry)
        }

        return Timeline(entries: entries, policy: .atEnd)
    }

    //    func relevances() async -> WidgetRelevances<ConfigurationAppIntent> {
    //        // Generate a list containing the contexts this widget is relevant in.
    //    }
}

struct SimpleEntry: TimelineEntry {
    let date: Date
    let configuration: ConfigurationAppIntent
}

struct GODeparturesEntryView: View {
    var entry: Provider.Entry

    var body: some View {
        VStack {
            Text("Union GO Station").bold()
            TripListItemView(
                trip: Trip(
                    id: "X1234",
                    code: "LE",
                    name: "Lakeshore East",
                    destination: "Durham College Oshawa GO",
                    platform: "5 & 6",
                    departureTime: .companion.fromEpochMilliseconds(
                        epochMilliseconds: 1234567
                    ),
                    lastUpdated: .companion.fromEpochMilliseconds(
                        epochMilliseconds: 0
                    ),
                    color: 0xFFDF1256,
                    tripOrder: 1,
                    info: "",
                    isVisible: true,
                    isCancelled: false,
                    isBus: false
                )
            )
            Text("Last updated: \(entry.date, style: .time)").font(.caption)
        }
    }
}

struct TripListItemView: View {
    var trip: coreKit.Trip
    init(trip: coreKit.Trip) {
        self.trip = trip
    }

    var body: some View {
        HStack(alignment: .center, spacing: 12) {
            Text("\(trip.departureDiffMinutes)\nmin").multilineTextAlignment(
                .center
            ).bold()
            ZStack {
                SquircleShape().frame(width: 30, height: 30).foregroundColor(Color(argb: trip.color))
                Text(trip.code).foregroundColor(.white).bold()
            }
            VStack(alignment: .leading) {
                Text(trip.destination)
                if trip.isCancelled {
                    Text("Cancelled").foregroundColor(.red).bold()
                } else if trip.isExpress {
                    Text("Express").foregroundColor(.green).bold()
                }
            }.frame(
                minWidth: 0,
                maxWidth: .infinity,
                minHeight: 0,
                maxHeight: .infinity
            )
            Text(trip.platform).foregroundColor(.green).bold()
        }
    }
}

struct GODepartures: Widget {
    let kind: String = "GODepartures"

    var body: some WidgetConfiguration {
        AppIntentConfiguration(
            kind: kind,
            intent: ConfigurationAppIntent.self,
            provider: Provider()
        ) { entry in
            GODeparturesEntryView(entry: entry)
                .containerBackground(.fill.tertiary, for: .widget)
        }
        // Limit to medium and large families only
        .supportedFamilies([.systemMedium, .systemLarge])
    }
}

extension ConfigurationAppIntent {
    fileprivate static var smiley: ConfigurationAppIntent {
        let intent = ConfigurationAppIntent()
        intent.favoriteEmoji = "😀"
        return intent
    }
    
    fileprivate static var starEyes: ConfigurationAppIntent {
        let intent = ConfigurationAppIntent()
        intent.favoriteEmoji = "🤩"
        return intent
    }
}

#Preview(as: .systemSmall) {
    GODepartures()
} timeline: {
    SimpleEntry(date: .now, configuration: .smiley)
    SimpleEntry(date: .now, configuration: .starEyes)
}

// MARK: - Color helpers for ARGB/RGB integers

extension Color {
    /// Initialize from ARGB 0xAARRGGBB
    init(argb: UInt64) {
        let a = Double((argb >> 24) & 0xFF) / 255.0
        let r = Double((argb >> 16) & 0xFF) / 255.0
        let g = Double((argb >> 8) & 0xFF) / 255.0
        let b = Double(argb & 0xFF) / 255.0
        self = Color(.sRGB, red: r, green: g, blue: b, opacity: a)
    }

    /// Initialize from RGB 0xRRGGBB (assumes full opacity)
    init(rgb: UInt64) {
        let r = Double((rgb >> 16) & 0xFF) / 255.0
        let g = Double((rgb >> 8) & 0xFF) / 255.0
        let b = Double(rgb & 0xFF) / 255.0
        self = Color(.sRGB, red: r, green: g, blue: b, opacity: 1.0)
    }

    /// Convenience that accepts either 0xAARRGGBB or 0xRRGGBB.
    /// If the value is greater than 0xFFFFFF, it's treated as ARGB; otherwise as RGB with full alpha.
    init(hex: UInt64) {
        if hex > 0xFFFFFF {
            self.init(argb: hex)
        } else {
            self.init(rgb: hex)
        }
    }
}
