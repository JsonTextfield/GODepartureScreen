//
//  GODepartures.swift
//  GODepartures
//
//  Created by Jason Bromfield on 2025-10-13.
//  Copyright © 2025 orgName. All rights reserved.
//

import AppIntents
import ComposeApp
import SwiftUI
import WidgetKit
import coreKit

struct Provider: AppIntentTimelineProvider {
    let widgetHelper = WidgetHelper()
    let goTrainDataSource: CoreIGoTrainDataSource
    let departureScreenUseCase: CoreGetSelectedStopUseCase

    init() {
        goTrainDataSource = widgetHelper.goTrainDataSource
        departureScreenUseCase = widgetHelper.getSelectedStopUseCase
    }

    func snapshot(
        for configuration: ConfigurationIntent,
        in context: Context,
        ) async -> SimpleEntry {
        let userDefaults = UserDefaults(
            suiteName: "group.com.jsontextfield.godepartures"
        )
        let selectedStopCode =
            configuration.selectedStop?.id
                ?? userDefaults?.object(
                forKey: "selectedStopCode"
            ) as? String
                ?? userDefaults?.object(
                forKey: "selectedStationCode"
            ) as? String
                ?? "UN"

        do {
            let allStops = try await goTrainDataSource.getAllStops()
            if let stop =
            allStops
                .first(where: {
                    $0.code.contains(selectedStopCode)
                })
                ?? allStops
                .first(where: {
                    $0.code.contains("UN")
                })
                ?? allStops.first {
                let trips: [CoreTrip]
                let sortMode = CoreSortMode.entries[
                    userDefaults?.integer(forKey: "sortMode") ?? 0
                    ]
                let visibleTrains: String =
                    userDefaults?.object(forKey: "hiddenTrains")
                        as? String ?? ""

                // Parse comma-separated stop codes
                let codes: [String] = stop.code
                    .split(separator: ",")
                    .map {
                        String($0)
                    }

                // Fetch trips per code (sequentially; safe for widgets)
                var fetchedTrips: [CoreTrip] = []
                for code in codes {
                    let result = try await goTrainDataSource.getTrips(
                        stopCode: code
                    )
                    fetchedTrips.append(contentsOf: result)
                }
                // Sort according to mode
                trips = fetchedTrips.filter { trip in
                    visibleTrains.isEmpty
                        || visibleTrains.contains(trip.code)
                }
                .sorted(by: {
                    switch configuration.sortMode {
                    case .time:
                        return $0.departureTime.toEpochMilliseconds()
                            < $1.departureTime.toEpochMilliseconds()
                    case .line:
                        fallthrough
                    default:
                        if $0.code == $1.code {
                            return $0.destination < $1.destination
                        }
                        return $0.code < $1.code
                    }
                })
                return SimpleEntry(
                    date: Date(),
                    stopName: stop.name,
                    trips: trips
                )
            }
        } catch {
        }

        return SimpleEntry(
            date: Date(),
            stopName: "",
            trips: []
        )
    }

    func timeline(
        for configuration: ConfigurationIntent,
        in context: Context,
        ) async -> Timeline<SimpleEntry> {
        let entry = await snapshot(for: configuration, in: context)
        return Timeline(entries: [entry], policy: .atEnd)
    }

    func placeholder(in context: Context) -> SimpleEntry {
        SimpleEntry(
            date: Date(),
            stopName: "Union GO Station",
            trips: [
                CoreTrip(
                    id: "X1234",
                    code: "LW",
                    name: "Lakeshore East",
                    destination: "Durham College Oshawa GO",
                    platform: "9 & 10",
                    departureTime: KotlinInstant.companion
                        .fromEpochMilliseconds(epochMilliseconds: 180_000),
                    lastUpdated: KotlinInstant.companion
                        .fromEpochMilliseconds(epochMilliseconds: 0),
                    color: 0xFF56_789F_0000_0000,
                    tripOrder: 1,
                    info: "Wait",
                    isVisible: true,
                    isCancelled: false,
                    isBus: true,
                    cars: nil,
                    busType: nil,
                    )
            ]
        )
    }

    //    func relevances() async -> WidgetRelevances<ConfigurationAppIntent> {
    //        // Generate a list containing the contexts this widget is relevant in.
    //    }
}

struct SimpleEntry: TimelineEntry {
    let date: Date
    let stopName: String
    let trips: [CoreTrip]
}

@main
struct GODepartures: Widget {
    let kind: String = "com.jsontextfield.godepartures.GODepartures"

    var body: some WidgetConfiguration {
        AppIntentConfiguration(
            kind: kind,
            intent: ConfigurationIntent.self,
            provider: Provider()
        ) { entry in
            GODeparturesEntryView(entry: entry).containerBackground(
                .fill.tertiary,
                for: .widget
            )
        }
        .configurationDisplayName("GO Departures")
        .description("Shows departure information for a stop")
        .supportedFamilies([
                               .systemSmall, .systemMedium, .systemLarge, .systemExtraLarge,
                           ]).contentMarginsDisabled()
    }

    init() {
        KoinKt.doInitKoin()
    }
}
