//
//  TripListItemView.swift
//  iosApp
//
//  Created by Jason Bromfield on 2025-10-15.
//  Copyright © 2025 orgName. All rights reserved.
//

import ComposeApp
import SwiftUI
import coreKit

struct TripListItemView: View {
    @Environment(\.colorScheme) var colorScheme
    let trip: CoreTrip
    let timeFormat: TimeFormat
    
    init(trip: CoreTrip, timeFormat: TimeFormat = .relative) {
        self.trip = trip
        self.timeFormat = timeFormat
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
        HStack(alignment: .center, spacing: 12) {
            TimeBox(trip: trip, timeFormat: timeFormat)
            TripCodeBox(trip: trip)
            VStack(alignment: .leading) {
                Text(trip.destination)
                    .lineLimit(2)
                if trip.isCancelled {
                    Text("cancelled")
                        .foregroundColor(errorColour)
                        .bold()
                        .font(.footnote)
                } else if trip.isExpress {
                    Text("express")
                        .foregroundColor(colour)
                        .bold()
                        .font(.footnote)
                }
            }.frame(maxWidth: .infinity, alignment: .leading)
            VStack {
                Text(trip.platform)
                    .foregroundColor(trip.platform == "-" ? nil : colour)
                    .bold()
                if let cars = trip.cars {
                    Text("\(cars) cars")
                        .font(.footnote)
                        .multilineTextAlignment(.center)
                } else if let busType = trip.busType {
                    Text(busType).font(.footnote)
                }
            }
            .frame(width: 64)
        }
    }
}
