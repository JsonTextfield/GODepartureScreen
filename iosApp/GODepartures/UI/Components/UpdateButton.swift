//
//  UpdateButton.swift
//  iosApp
//
//  Created by Jason Bromfield on 2026-09-11.
//  Copyright © 2026 orgName. All rights reserved.
//

import AppIntents
import SwiftUI

struct UpdateButton: View {
    let title: LocalizedStringKey

    var body: some View {
        Button(intent: RefreshIntent()) {
            HStack {
                Image(systemName: "arrow.clockwise")
                Text(title).font(.footnote)
            }
        }
    }
}
