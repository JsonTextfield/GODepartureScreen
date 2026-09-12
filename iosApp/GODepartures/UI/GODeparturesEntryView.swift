//
//  GODeparturesEntryView.swift
//  GODeparturesExtension
//
//  Created by Jason Bromfield on 2025-11-13.
//  Copyright © 2025 orgName. All rights reserved.
//

import Algorithms
import AppIntents
import ComposeApp
import SwiftUI
import WidgetKit

struct GODeparturesEntryView: View {
    var entry: SimpleEntry
    @Environment(\.widgetFamily) var widgetFamily: WidgetFamily

    init(entry: SimpleEntry) {
        self.entry = entry
    }

    var body: some View {
        VStack {
            Link(
                destination: URL(
                    string: "go-departures://app/stops/\(entry.stop.name)"
                )!
            ) {
                Text(entry.stop.name)
                    .lineLimit(1)
                    .font(.footnote)
                    .bold()
                    .padding(.vertical, 4)
                    .frame(minWidth: 0, maxWidth: .infinity)
            }
            if widgetFamily == .systemSmall {
                SmallWidgetView(entry: entry)
            } else {
                let columnCount = widgetFamily == .systemExtraLarge ? 2 : 1
                let rowCount = widgetFamily == .systemMedium ? 1 : 4
                Grid {
                    GridRow {
                        ForEach(
                            0..<(min(columnCount, entry.trips.count)),
                            id: \.self
                        ) { _ in
                            WidgetTripListHeaderRow()
                        }
                    }
                    ForEach(
                        entry.trips
                            .chunks(ofCount: columnCount)
                            .prefix(rowCount),
                        id: \.self.first?.id,
                    ) { trips in
                        GridRow {
                            ForEach(trips, id: \.self.id) { trip in
                                Link(
                                    destination: entry.getTripDestination(
                                        trip: trip
                                    )
                                ) {
                                    TripListItemView(
                                        trip: trip,
                                        timeFormat: entry.timeFormat,
                                    )
                                }
                            }
                        }.frame(maxHeight: .infinity, alignment: .top)
                    }
                }
                Spacer()
                UpdateButton(title: "Last updated: \(entry.date, style: .time)")
            }
        }.frame(maxHeight: .infinity).padding(4)
    }
}
