//
//  TripListItemView.swift
//  iosApp
//
//  Created by Jason Bromfield on 2025-10-15.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import coreKit

struct TripListItemView: View {
    var trip: Trip

    var body: some View {
        HStack(alignment: .center, spacing: 12) {
            Text("\(trip.departureDiffMinutes)\nmin")
                .multilineTextAlignment(.center)
                .minimumScaleFactor(0.5)
                .bold()
            ZStack(alignment: .center) {
                SquircleShape()
                    .frame(width: 32, height: 32)
                    .foregroundColor(Color(argb: trip.color >> 32))
                Text(trip.code)
                    .foregroundColor(.white)
                    .bold()
            }
            VStack(alignment: .leading) {
                Text(trip.destination)
                    .minimumScaleFactor(0.9)
                if trip.isCancelled {
                    Text("cancelled")
                        .foregroundColor(.red)
                        .bold()
                        .font(.footnote)
                        .minimumScaleFactor(0.9)
                } else if trip.isExpress {
                    Text("express")
                        .foregroundColor(.green)
                        .bold()
                        .font(.footnote)
                        .minimumScaleFactor(0.9)
                }
            }.frame(maxWidth: .infinity, alignment: .leading)
            Text(trip.platform)
                .foregroundColor(trip.platform == "-" ? nil : .green)
                .bold()
                .frame(maxWidth: .infinity)
        }.padding(EdgeInsets(top: 8, leading: 4, bottom: 8, trailing: 4))
    }
}

#Preview {
    TripListItemView(
        trip: Trip(
            id: "X1234",
            code: "LE",
            name: "Lakeshore East",
            destination: "Durham College Oshawa GO",
            platform: "5 & 6",
            departureTime: .companion.fromEpochMilliseconds(
                epochMilliseconds: 1_234_567
            ),
            lastUpdated: .companion.fromEpochMilliseconds(
                epochMilliseconds: 0
            ),
            color: 0xFFDF_1256,
            tripOrder: 1,
            info: "",
            isVisible: true,
            isCancelled: false,
            isBus: false
        )
    )
}
