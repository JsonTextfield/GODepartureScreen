//
//  StationDetail.swift
//  GODeparturesExtension
//
//  Created by Jason Bromfield on 2025-11-09.
//  Copyright © 2025 orgName. All rights reserved.
//

import AppIntents
import ComposeApp
import Foundation
import WidgetKit

struct StationDetail: AppEntity {

    let id: String
    let name: String

    static var typeDisplayRepresentation: TypeDisplayRepresentation = "Station"
    static var defaultQuery = StationQuery()

    var displayRepresentation: DisplayRepresentation {
        DisplayRepresentation(title: "\(name)")
    }

    static var allStations: [StationDetail] = []
}

struct StationQuery: EntityQuery {
    func entities(for identifiers: [StationDetail.ID]) async throws
        -> [StationDetail]
    {
        return StationDetail.allStations.filter { identifiers.contains($0.id) }
    }

    func suggestedEntities() async throws -> [StationDetail] {
        let widgetHelper = WidgetHelper()
        let departureScreenUseCase = widgetHelper.departureScreenUseCase

        StationDetail.allStations =
            try await departureScreenUseCase
            .getFavouriteStations()
            .map { station in
                StationDetail(id: station.code, name: station.name)
            }
        return StationDetail.allStations
    }

    func defaultResult() async -> StationDetail? {
        try? await suggestedEntities().first(where: { $0.id.contains("UN") })
    }
}
