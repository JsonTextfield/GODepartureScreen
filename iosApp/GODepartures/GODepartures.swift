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

struct Provider: TimelineProvider {
    func getSnapshot(
        in context: Context,
        completion: @escaping @Sendable (SimpleEntry) -> Void
    ) {
        let goTrainDataSource: IGoTrainDataSource = GoTrainDataSource(
            departureScreenAPI: DepartureScreenAPI()
        )

        Task {
            let trips: [Trip]
            do {
                trips = try await goTrainDataSource.getTrains(stationCode: "UN")
            } catch {
                trips = []
            }
            completion(
                SimpleEntry(
                    date: Date(),
                    stationName: "Union GO Station",
                    trips: trips,
                )
            )
        }
    }

    func getTimeline(
        in context: Context,
        completion: @escaping @Sendable (Timeline<SimpleEntry>) -> Void
    ) {
        getSnapshot(in: context) { entry in
            completion(Timeline(entries: [entry], policy: .never))
        }
    }

    func placeholder(in context: Context) -> SimpleEntry {
        SimpleEntry(
            date: Date(),
            stationName: "Union GO Station",
            trips: [
                Trip(
                    id: "X1234",
                    code: "LW",
                    name: "Lakeshore East",
                    destination: "Durham College Oshawa GO",
                    platform: "9 & 10",
                    departureTime: Kotlinx_datetimeInstant.companion
                        .fromEpochMilliseconds(epochMilliseconds: 180_000),
                    lastUpdated: Kotlinx_datetimeInstant.companion
                        .fromEpochMilliseconds(epochMilliseconds: 0),
                    color: 0xFF56_789F_0000_0000,
                    tripOrder: 1,
                    info: "Wait",
                    isVisible: true,
                    isCancelled: false,
                    isBus: true,
                )
            ]
        )
    }

    //    func relevances() async -> WidgetRelevances<ConfigurationAppIntent> {
    //        // Generate a list containing the contexts this widget is relevant in.
    //    }
}

struct SimpleEntry: TimelineEntry {
    let date: Date
    let stationName: String
    let trips: [Trip]
}

struct GODeparturesEntryView: View {
    var entry: SimpleEntry
    @Environment(\.widgetFamily) var widgetFamily: WidgetFamily

    init(entry: SimpleEntry) {
        self.entry = entry
    }

    var body: some View {
        VStack {
            Text(entry.stationName).font(.footnote).bold()
            switch widgetFamily {
            case .systemMedium:
                if !entry.trips.isEmpty {
                    TripListItemView(
                        trip: entry.trips.first!
                    )
                }
            case .systemLarge:
                ForEach(entry.trips.prefix(4), id: \.self) { trip in
                    TripListItemView(
                        trip: trip
                    )
                }
            case .systemExtraLarge:
                Grid {
                    ForEach(entry.trips.chunked(into: 2).prefix(4), id: \.self)
                    { trips in
                        GridRow {
                            ForEach(trips, id: \.self) { trip in
                                TripListItemView(
                                    trip: trip
                                )
                            }
                        }
                    }
                }
            default:
                Spacer()
            }
            Text("Last updated: \(entry.date, style: .time)").font(.caption)
        }
    }
}

@main
struct GODepartures: Widget {
    let kind: String = "com.jsontextfield.godepartures.GODepartures"

    var body: some WidgetConfiguration {
        StaticConfiguration(
            kind: kind,
            provider: Provider()
        ) { entry in
            GODeparturesEntryView(entry: entry).containerBackground(
                .fill.tertiary,
                for: .widget
            )
        }
        .description("Shows departure information for a station")
        .supportedFamilies([.systemMedium, .systemLarge, .systemExtraLarge])
    }
}

#Preview(as: .systemMedium) {
    GODepartures()
} timeline: {
    SimpleEntry(
        date: .now,
        stationName: "Union GO Station",
        trips: [
            Trip(
                id: "X1234",
                code: "LW",
                name: "Lakeshore East",
                destination: "Durham College Oshawa GO",
                platform: "9 & 10",
                departureTime: Kotlinx_datetimeInstant.companion
                    .fromEpochMilliseconds(epochMilliseconds: 180_000),
                lastUpdated: Kotlinx_datetimeInstant.companion
                    .fromEpochMilliseconds(epochMilliseconds: 0),
                color: 0xFF56_789F_0000_0000,
                tripOrder: 1,
                info: "Wait",
                isVisible: true,
                isCancelled: false,
                isBus: true,
            )
        ]
    )
}
extension Array {
    func chunked(into size: Int) -> [[Element]] {
        return stride(from: 0, to: count, by: size).map {
            Array(self[$0..<Swift.min($0 + size, count)])
        }
    }
}
