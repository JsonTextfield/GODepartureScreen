//
//  GODeparturesEntryView.swift
//  GODeparturesExtension
//
//  Created by Jason Bromfield on 2025-11-13.
//  Copyright © 2025 orgName. All rights reserved.
//

import Algorithms
import AppIntents
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
            Text(entry.stationName)
                .font(.footnote)
                .bold()
            if !entry.trips.isEmpty {
                let columnCount = widgetFamily == .systemExtraLarge ? 2 : 1
                let rowCount = widgetFamily == .systemMedium ? 1 : 4
                Grid {
                    ForEach(
                        entry.trips
                            .chunks(ofCount: columnCount)
                            .prefix(rowCount),
                        id: \.self.first?.id,
                    ) { trips in
                        GridRow {
                            ForEach(trips, id: \.self.id) { trip in
                                TripListItemView(
                                    trip: trip
                                )
                            }
                        }.frame(maxHeight: .infinity)
                    }
                }
            }
            Spacer()
            Button(intent: RefreshIntent()) {
                HStack {
                    Image(systemName: "arrow.clockwise")
                    Text("Last updated: \(entry.date, style: .time)").font(.footnote)
                }
            }

        }.frame(maxHeight: .infinity)
    }
}
