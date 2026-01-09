//
//  TripCodeBox.swift
//  GODeparturesExtension
//
//  Created by Jason Bromfield on 2025-11-14.
//  Copyright © 2025 orgName. All rights reserved.
//

import ComposeApp
import SwiftUI

struct TripCodeBox: View {
    let trip: CoreTrip
    init(trip: CoreTrip) {
        self.trip = trip
    }
    var body: some View {
        ZStack(alignment: .center) {
            SquircleShape()
                .frame(width: 32, height: 32)
                .foregroundColor(Color(argb: trip.color))
            Text(trip.code)
                .foregroundColor(.white)
                .bold()
        }
    }
}
