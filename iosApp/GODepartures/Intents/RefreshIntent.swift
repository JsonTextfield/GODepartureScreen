//
//  RefreshIntent.swift
//  GODeparturesExtension
//
//  Created by Jason Bromfield on 2025-11-13.
//  Copyright © 2025 orgName. All rights reserved.
//
import AppIntents
import WidgetKit

struct RefreshIntent: AppIntent {
    static var title: LocalizedStringResource = "Update"
    static var description = IntentDescription("Update the departure screen.")
    func perform() async throws -> some IntentResult {
        WidgetCenter.shared.reloadAllTimelines()
        return .result()
    }
}
