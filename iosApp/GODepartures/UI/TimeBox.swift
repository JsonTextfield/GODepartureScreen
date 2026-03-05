//
//  TimeBox.swift
//  iosApp
//
//  Created by Jason Bromfield on 2026-03-04.
//  Copyright © 2026 orgName. All rights reserved.
//

import ComposeApp
import SwiftUI

struct TimeBox: View {
    let trip: CoreTrip
    let timeFormat: TimeFormat
    init(trip: CoreTrip, timeFormat: TimeFormat = .relative) {
        self.trip = trip
        self.timeFormat = timeFormat
    }

    var body: some View {
        VStack(alignment: .center, spacing: 0) {
            if timeFormat == .relative {
                Text("\(trip.relativeDepartureTime)")
                    .multilineTextAlignment(.center)
                    .bold()
                Text("min")
                    .font(.footnote)
                    .multilineTextAlignment(.center)
                    .bold()
            } else {
                Text("\(trip.twentyFourHourDepartureTime)")
                    .font(.caption)
                    .multilineTextAlignment(.center)
                    .bold()
            }
        }.frame(width: 50)
    }
}
