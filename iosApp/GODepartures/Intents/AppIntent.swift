//
//  AppIntent.swift
//  AlertsWidget
//
//  Created by Jason Bromfield on 2026-09-20.
//  Copyright © 2026 orgName. All rights reserved.
//

import WidgetKit
import AppIntents

struct ConfigurationAppIntent: WidgetConfigurationIntent {
    static var title: LocalizedStringResource = "Widget settings"
    static var description = IntentDescription("Configure the GO Departures widget.")
}
