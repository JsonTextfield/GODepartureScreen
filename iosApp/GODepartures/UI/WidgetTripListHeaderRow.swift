//
//  WidgetTripListHeaderRow.swift
//  GODeparturesExtension
//
//  Created by Jason Bromfield on 2025-11-29.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct WidgetTripListHeaderRow: View {
    var body: some View {
        HStack(alignment: .center, spacing: 12) {
            Text("Time")
                .lineLimit(1)
                .font(.footnote)
                .frame(width: 40)
            Text("Line")
                .lineLimit(1)
                .font(.footnote)
                .frame(maxWidth: .infinity)
            Text("Platform")
                .lineLimit(1)
                .font(.footnote)
                .frame(width: 64)
        }
    }
}
