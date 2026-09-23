//
//  AlertsWidget.swift
//  AlertsWidget
//
//  Created by Jason Bromfield on 2026-09-20.
//  Copyright © 2026 orgName. All rights reserved.
//

import ComposeApp
import KMPNativeCoroutinesAsync
import KMPNativeCoroutinesCore
import SwiftUI
import WidgetKit
import coreKit

@MainActor
struct AlertsWidgetProvider: AppIntentTimelineProvider {
    func placeholder(in context: Context) -> AlertsEntry {
        AlertsEntry(
            date: Date(),
            configuration: ConfigurationAppIntent(),
            alerts: []
        )
    }

    func snapshot(
        for configuration: ConfigurationAppIntent,
        in context: Context
    ) async -> AlertsEntry {
        var allAlerts: [CoreAlert] = []

        let widgetHelper: WidgetHelper = WidgetHelper()

        do {
            let sequence = asyncSequence(
                for: widgetHelper.transitRepository.getAllAlerts(language: "en")
            )
            for try await alerts in sequence {
                allAlerts = Array(Set(alerts)).sorted(by: { alert1, alert2 in
                    return alert1.date.toEpochMilliseconds()
                        > alert2.date.toEpochMilliseconds()
                })
                break
            }
        } catch {
            print("Failed with error: \(error)")
        }

        return AlertsEntry(
            date: Date(),
            configuration: configuration,
            alerts: allAlerts
        )
    }

    func timeline(
        for configuration: ConfigurationAppIntent,
        in context: Context
    ) async -> Timeline<AlertsEntry> {
        let entry = await snapshot(for: configuration, in: context)

        return Timeline(entries: [entry], policy: .atEnd)
    }

    //    func relevances() async -> WidgetRelevances<ConfigurationAppIntent> {
    //        // Generate a list containing the contexts this widget is relevant in.
    //    }
}

struct AlertsEntry: TimelineEntry {
    let date: Date
    let configuration: ConfigurationAppIntent
    let alerts: [CoreAlert]
}

struct AlertsWidget: Widget {
    var body: some WidgetConfiguration {
        AppIntentConfiguration(
            kind: "AlertsWidget",
            intent: ConfigurationAppIntent.self,
            provider: AlertsWidgetProvider()
        ) { entry in
            AlertsWidgetEntryView(entry: entry)
                .containerBackground(.fill.tertiary, for: .widget)
        }
        .configurationDisplayName("Alerts")
        .description("Displays the most recent GO Transit alerts")
    }
}
