//
//  GODeparturesLiveActivity.swift
//  GODepartures
//
//  Created by Jason Bromfield on 2025-10-13.
//  Copyright © 2025 orgName. All rights reserved.
//

import ActivityKit
import WidgetKit
import SwiftUI

struct GODeparturesAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        // Dynamic stateful properties about your activity go here!
        var emoji: String
    }

    // Fixed non-changing properties about your activity go here!
    var name: String
}

struct GODeparturesLiveActivity: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: GODeparturesAttributes.self) { context in
            // Lock screen/banner UI goes here
            VStack {
                Text("Hello \(context.state.emoji)")
            }
            .activityBackgroundTint(Color.cyan)
            .activitySystemActionForegroundColor(Color.black)

        } dynamicIsland: { context in
            DynamicIsland {
                // Expanded UI goes here.  Compose the expanded UI through
                // various regions, like leading/trailing/center/bottom
                DynamicIslandExpandedRegion(.leading) {
                    Text("Leading")
                }
                DynamicIslandExpandedRegion(.trailing) {
                    Text("Trailing")
                }
                DynamicIslandExpandedRegion(.bottom) {
                    Text("Bottom \(context.state.emoji)")
                    // more content
                }
            } compactLeading: {
                Text("L")
            } compactTrailing: {
                Text("T \(context.state.emoji)")
            } minimal: {
                Text(context.state.emoji)
            }
            .widgetURL(URL(string: "http://www.apple.com"))
            .keylineTint(Color.red)
        }
    }
}

extension GODeparturesAttributes {
    fileprivate static var preview: GODeparturesAttributes {
        GODeparturesAttributes(name: "World")
    }
}

extension GODeparturesAttributes.ContentState {
    fileprivate static var smiley: GODeparturesAttributes.ContentState {
        GODeparturesAttributes.ContentState(emoji: "😀")
     }
     
     fileprivate static var starEyes: GODeparturesAttributes.ContentState {
         GODeparturesAttributes.ContentState(emoji: "🤩")
     }
}

#Preview("Notification", as: .content, using: GODeparturesAttributes.preview) {
   GODeparturesLiveActivity()
} contentStates: {
    GODeparturesAttributes.ContentState.smiley
    GODeparturesAttributes.ContentState.starEyes
}
