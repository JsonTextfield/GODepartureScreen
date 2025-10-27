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
    var trip: CoreTrip

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
            Text("\(trip.departureDiffMinutes)\nmin")
                .multilineTextAlignment(.center)
                .bold()
            ZStack(alignment: .center) {
                SquircleShape()
                    .frame(width: 32, height: 32)
                    .foregroundColor(Color(argb: trip.color))
                Text(trip.code)
                    .foregroundColor(.white)
                    .bold()
            }
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
            Text(trip.platform)
                .foregroundColor(trip.platform == "-" ? nil : colour)
                .bold()
                .frame(maxWidth: .infinity)
        }
    }
}
