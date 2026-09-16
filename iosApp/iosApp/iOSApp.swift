import ComposeApp
import SwiftUI
import WidgetKit
import GoogleMobileAds

@main
struct iOSApp: App {
    init() {
        KoinKt.doInitKoin()
        GoogleMobileAds.MobileAds.shared.start()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
