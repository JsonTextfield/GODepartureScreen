//
//  MyAppWidgets.swift
//  iosApp
//
//  Created by Jason Bromfield on 2026-09-22.
//  Copyright © 2026 orgName. All rights reserved.
//

import WidgetKit
import SwiftUI
import ComposeApp
import coreKit

@main
struct GoDeparturesWidgetBundle: WidgetBundle {
    var body: some Widget {
        GODepartures()
        AlertsWidget()
    }
    
    init() {
        KoinKt.doInitKoin()
    }
}
