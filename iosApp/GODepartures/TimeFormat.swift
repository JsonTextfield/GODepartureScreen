//
//  TimeFormat.swift
//  GODeparturesExtension
//
//  Created by Jason Bromfield on 2026-03-04.
//  Copyright © 2026 orgName. All rights reserved.
//

import Foundation
import AppIntents

enum TimeFormat: String, AppEnum, CaseIterable {
    case twentyFourHour
    case relative

    static var typeDisplayRepresentation: TypeDisplayRepresentation = "Time format"

    static var caseDisplayRepresentations: [TimeFormat: DisplayRepresentation] = [
        .twentyFourHour: "24-hour",
        .relative: "Relative"
    ]
}
