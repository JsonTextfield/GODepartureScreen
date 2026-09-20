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
struct Provider: AppIntentTimelineProvider {
    func placeholder(in context: Context) -> SimpleEntry {
        SimpleEntry(
            date: Date(),
            configuration: ConfigurationAppIntent(),
            alerts: []
        )
    }

    func snapshot(
        for configuration: ConfigurationAppIntent,
        in context: Context
    ) async -> SimpleEntry {
        SimpleEntry(date: Date(), configuration: configuration, alerts: [])
    }

    func timeline(
        for configuration: ConfigurationAppIntent,
        in context: Context
    ) async -> Timeline<SimpleEntry> {
        var allAlerts: [CoreAlert] = []

        let widgetHelper: WidgetHelper = WidgetHelper()

        do {
            let sequence = asyncSequence(
                for: widgetHelper.transitRepository.getAllAlerts(language: "en")
            )
            for try await alerts in sequence {
                allAlerts = Array(Set(alerts)).sorted(by: { alert1, alert2 in
                    return alert2.date.toEpochMilliseconds()
                        > alert1.date.toEpochMilliseconds()
                })
                break
            }
        } catch {
            print("Failed with error: \(error)")
        }

        let entry = SimpleEntry(
            date: Date(),
            configuration: configuration,
            alerts: allAlerts
        )

        return Timeline(entries: [entry], policy: .atEnd)
    }

    //    func relevances() async -> WidgetRelevances<ConfigurationAppIntent> {
    //        // Generate a list containing the contexts this widget is relevant in.
    //    }
}

struct SimpleEntry: TimelineEntry {
    let date: Date
    let configuration: ConfigurationAppIntent
    let alerts: [CoreAlert]
}

struct AlertsWidget: Widget {
    let kind: String = "AlertsWidget"

    var body: some WidgetConfiguration {
        AppIntentConfiguration(
            kind: kind,
            intent: ConfigurationAppIntent.self,
            provider: Provider()
        ) { entry in
            AlertsWidgetEntryView(entry: entry)
                .containerBackground(.fill.tertiary, for: .widget)
        }
    }

    init() {
        KoinKt.doInitKoin()
    }
}

extension ConfigurationAppIntent {
    fileprivate static var smiley: ConfigurationAppIntent {
        let intent = ConfigurationAppIntent()
        intent.favoriteEmoji = "😀"
        return intent
    }

    fileprivate static var starEyes: ConfigurationAppIntent {
        let intent = ConfigurationAppIntent()
        intent.favoriteEmoji = "🤩"
        return intent
    }
}
