//
//  GODeparturesBundle.swift
//  GODepartures
//
//  Created by Jason Bromfield on 2025-10-13.
//  Copyright © 2025 orgName. All rights reserved.
//

import WidgetKit
import SwiftUI

@main
struct GODeparturesBundle: WidgetBundle {
    var body: some Widget {
        GODepartures()
        GODeparturesControl()
        GODeparturesLiveActivity()
    }
}
