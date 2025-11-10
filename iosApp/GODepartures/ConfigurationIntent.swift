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
    static var title: LocalizedStringResource = "Select station"
    static var description = IntentDescription("Select the station to view departure information for.")
    
    @Parameter(title: "Selected station")
    var selectedStation: StationDetail?
    
    init(selectedStation: StationDetail? = nil) {
        self.selectedStation = selectedStation
    }
    
    init() {
        self.selectedStation = nil
    }
}
