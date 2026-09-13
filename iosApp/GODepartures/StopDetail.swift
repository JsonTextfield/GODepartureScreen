//
//  StopDetail.swift
//  GODeparturesExtension
//
//  Created by Jason Bromfield on 2025-11-09.
//  Copyright © 2025 orgName. All rights reserved.
//

import AppIntents
import ComposeApp
import Foundation
import WidgetKit
import coreKit

struct StopDetail: AppEntity {

    let id: String
    let name: String

    static var typeDisplayRepresentation: TypeDisplayRepresentation = "Stop"
    static var defaultQuery = StopQuery()

    var displayRepresentation: DisplayRepresentation {
        DisplayRepresentation(title: "\(name)", subtitle: "\(id)")
    }

    static var allStops: [StopDetail] = []
}

struct StopQuery: EntityQuery {
    func entities(for identifiers: [StopDetail.ID]) async throws
        -> [StopDetail]
    {
        return StopDetail.allStops.filter { identifiers.contains($0.id) }
    }

    func suggestedEntities() async throws -> [StopDetail] {
        let userDefaults = UserDefaults(suiteName: "group.com.jsontextfield.godepartures")
        let favourites = userDefaults?.object(forKey: "favouriteStops") as? String ?? userDefaults?.object(forKey: "favouriteStations") as? String ?? ""
        let descriptors: [SortDescriptor<StopDetail>] = [
            .transform({ stop in
                let codes: [String] = stop.id.components(separatedBy: ", ")
                return codes.contains(where: { code in
                    favourites.contains(code)
                }) ? 1 : 0
            }, order: .reverse),
            .transform({
                ($0.id.contains("UN") || $0.id.contains("02300")) ? 1 : 0
            }, order: .reverse),
            .string(\.name, compare: { $0.localizedCaseInsensitiveCompare($1) })
        ]
        
        let widgetHelper = WidgetHelper()
        let allStops = try await widgetHelper.transitRepository.getAllStops()
        let groupedStops = Dictionary(grouping: allStops, by: { $0.name })
            .map {
                StopDetail(id: $0.value.map{$0.code}.joined(separator: ", "), name: $0.key)
            }
            .sorted(using: descriptors)
        StopDetail.allStops = groupedStops
        return StopDetail.allStops
    }

    func defaultResult() async -> StopDetail? {
        try? await suggestedEntities().first(where: { $0.id.contains("UN") })
    }
}

// MARK: - Lightweight Kotlin-like sorting helpers

enum SortOrder {
    case forward
    case reverse
}

struct SortDescriptor<Value> {
    let compare: (Value, Value) -> ComparisonResult

    // Convenience makers

    // Comparable key-path
    static func keyPath<T: Comparable>(
        _ keyPath: KeyPath<Value, T>,
        order: SortOrder = .forward
    ) -> SortDescriptor<Value> {
        SortDescriptor { lhs, rhs in
            let l = lhs[keyPath: keyPath]
            let r = rhs[keyPath: keyPath]
            if l == r { return .orderedSame }
            let less = l < r
            let asc = (order == .forward) ? less : !less
            return asc ? .orderedAscending : .orderedDescending
        }
    }

    // Bool key-path (true first when order == .reverse)
    static func boolean(
        _ keyPath: KeyPath<Value, Bool>,
        order: SortOrder = .forward
    ) -> SortDescriptor<Value> {
        // Compare directly using the Bool key-path, mapping to Int for ordering
        SortDescriptor { lhs, rhs in
            let l = lhs[keyPath: keyPath] ? 1 : 0
            let r = rhs[keyPath: keyPath] ? 1 : 0
            if l == r { return .orderedSame }
            let less = l < r
            let asc = (order == .forward) ? less : !less
            return asc ? .orderedAscending : .orderedDescending
        }
    }

    // Case-insensitive or custom string compare via key-path
    static func string(
        _ keyPath: KeyPath<Value, String>,
        order: SortOrder = .forward,
        compare: @escaping (String, String) -> ComparisonResult
    ) -> SortDescriptor<Value> {
        SortDescriptor { lhs, rhs in
            let result = compare(lhs[keyPath: keyPath], rhs[keyPath: keyPath])
            switch order {
            case .forward: return result
            case .reverse:
                switch result {
                case .orderedAscending: return .orderedDescending
                case .orderedDescending: return .orderedAscending
                case .orderedSame: return .orderedSame
                }
            }
        }
    }

    // Derived Comparable value (single-parameter selector)
    static func transform<T: Comparable>(
        _ transform: @escaping (Value) -> T,
        order: SortOrder = .forward
    ) -> SortDescriptor<Value> {
        SortDescriptor { lhs, rhs in
            let l = transform(lhs)
            let r = transform(rhs)
            if l == r { return .orderedSame }
            let less = l < r
            let asc = (order == .forward) ? less : !less
            return asc ? .orderedAscending : .orderedDescending
        }
    }

    // Helper to map a descriptor through a transform (kept for completeness)
    func map<Mapped>(
        _ transform: @escaping (Value) -> Mapped
    ) -> SortDescriptor<Value> where Mapped: Comparable {
        SortDescriptor<Value> { lhs, rhs in
            let l = transform(lhs)
            let r = transform(rhs)
            if l == r { return .orderedSame }
            return l < r ? .orderedAscending : .orderedDescending
        }
    }
}

extension Array {
    func sorted(using descriptors: [SortDescriptor<Element>]) -> [Element] {
        guard !descriptors.isEmpty else { return self }
        return self.sorted { a, b in
            for descriptor in descriptors {
                let result = descriptor.compare(a, b)
                if result != .orderedSame {
                    return result == .orderedAscending
                }
            }
            return false
        }
    }
}
