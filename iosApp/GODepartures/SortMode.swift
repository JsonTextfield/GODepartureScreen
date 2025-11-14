//
//  SwiftSortMode.swift
//  GODeparturesExtension
//
//  Created by Jason Bromfield on 2025-11-13.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import AppIntents

enum SortMode: String, AppEnum, CaseIterable {
    case time
    case line

    static var typeDisplayRepresentation: TypeDisplayRepresentation = "Sort mode"

    static var caseDisplayRepresentations: [SortMode: DisplayRepresentation] = [
        .time: "Time",
        .line: "Line"
    ]
}
