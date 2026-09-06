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

    @Parameter(title: "Stop")
    var selectedStop: StopDetail?

    @Parameter(title: "Sort by…", default: .time)
    var sortMode: SortMode
    
    @Parameter(title: "Time format", default: .relative)
    var timeFormat: TimeFormat

    init(
        selectedStop: StopDetail? = nil,
        sortMode: SortMode,
        timeFormat: TimeFormat,
    ) {
        self.selectedStop = selectedStop
        self.sortMode = sortMode
        self.timeFormat = timeFormat
    }
    
    init() {}
}
