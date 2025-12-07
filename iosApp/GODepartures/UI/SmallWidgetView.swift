//
//  SmallWidgetView.swift
//  GODeparturesExtension
//
//  Created by Jason Bromfield on 2025-12-07.
//  Copyright © 2025 orgName. All rights reserved.
//

import AppIntents
import ComposeApp
import SwiftUI
import coreKit

struct SmallWidgetView: View {
    @Environment(\.colorScheme) var colorScheme

    var entry: SimpleEntry

    init(entry: SimpleEntry) {
        self.entry = entry
    }

    var body: some View {
        let colour = Color(
            argb: (colorScheme == .dark
                ? coreKit.ColourKt.primaryDark : coreKit.ColourKt.primaryLight)
        )
        let errorColour = Color(
            argb: (colorScheme == .dark
                ? coreKit.ColourKt.errorDark : coreKit.ColourKt.errorLight)
        )
        if let trip = entry.trips.first {
            VStack {
                HStack {
                    TripCodeBox(trip: trip)
                    Text(trip.destination)
                        .lineLimit(2)
                        .font(.footnote)
                }.frame(maxHeight: .infinity)
                HStack {
                    VStack(alignment: .center) {
                        Text("Time")
                            .lineLimit(1)
                            .font(.footnote)
                        Text("\(trip.departureDiffMinutes) min")
                            .font(.footnote)
                            .multilineTextAlignment(.center)
                            .bold()
                    }.frame(maxWidth: .infinity)
                    VStack(alignment: .center) {
                        Text("Platform")
                            .lineLimit(1)
                            .font(.footnote)
                        Text(trip.platform)
                            .font(.footnote)
                            .foregroundColor(trip.platform == "-" ? nil : colour)
                            .bold()
                    }.frame(maxWidth: .infinity)
                }.frame(maxHeight: .infinity)
                Button(intent: RefreshIntent()) {
                    HStack {
                        Image(systemName: "arrow.clockwise")
                        Text("\(entry.date, style: .time)")
                            .font(.footnote)
                    }
                }
            }
        }
    }
}
