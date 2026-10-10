//
//  TripCodeBox.swift
//  GODeparturesExtension
//
//  Created by Jason Bromfield on 2025-11-14.
//  Copyright © 2025 orgName. All rights reserved.
//

import ComposeApp
import SwiftUI
import WidgetKit

struct TripCodeBox: View {
    let trip: CoreTrip

    @Environment(\.widgetRenderingMode) private var renderingMode
    @ScaledMetric(relativeTo: .subheadline) private var scaledBoxSize: CGFloat = 32

    private var boxSize: CGFloat {
        min(max(scaledBoxSize, 32), 40)
    }

    var body: some View {
        ZStack(alignment: .center) {
            switch renderingMode {
            case .accented:
                // No solid fill here — a filled shape + accentable text
                // on top both tint to the same color and cancel out.
                // Use an outline instead, and only accent the outline.
                SquircleShape()
                    .stroke(lineWidth: 2)
                    .widgetAccentable()

                Text(trip.code)
                    .bold()
                    // left un-accented on purpose: it renders at the
                    // system's dimmer tint tier, giving contrast
                    // against the accented outline

            default: // .fullColor, .vibrant, etc.
                SquircleShape()
                    .foregroundColor(Color(argb: trip.color))

                Text(trip.code)
                    .foregroundColor(.white)
                    .bold()
            }
        }
        .frame(width: boxSize, height: boxSize)
    }
}
