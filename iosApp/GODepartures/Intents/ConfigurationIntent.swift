//
//  ConfigurationIntent.swift
//  GODeparturesExtension
//
//  Created by Jason Bromfield on 2025-11-09.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import WidgetKit
import AppIntents

struct ConfigurationIntent: WidgetConfigurationIntent {
    static var title: LocalizedStringResource = "Widget settings"
    static var description = IntentDescription("Configure the GO Departures widget.")
    
    @Parameter(title: "Station")
    var selectedStation: StationDetail?
    
    @Parameter(title: "Sort by...")
    var sortMode: SortMode?
    
    init(
        selectedStation: StationDetail? = nil,
        sortMode: SortMode? = SortMode.time,
    ) {
        self.selectedStation = selectedStation
        self.sortMode = sortMode
    }
    
    init() {
        self.selectedStation = nil
        self.sortMode = SortMode.time
    }
}
