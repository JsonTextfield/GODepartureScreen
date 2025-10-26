//
//  ColorExtension.swift
//  iosApp
//
//  Created by Jason Bromfield on 2025-10-15.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

extension Color {
    /// Initialize from ARGB 0xAARRGGBB
    init(argb: UInt64) {
        let a = Double((argb >> 24) & 0xFF) / 255.0
        let r = Double((argb >> 16) & 0xFF) / 255.0
        let g = Double((argb >> 8) & 0xFF) / 255.0
        let b = Double(argb & 0xFF) / 255.0
        self = Color(.sRGB, red: r, green: g, blue: b, opacity: a)
    }

    /// Initialize from RGB 0xRRGGBB (assumes full opacity)
    init(rgb: UInt64) {
        let r = Double((rgb >> 16) & 0xFF) / 255.0
        let g = Double((rgb >> 8) & 0xFF) / 255.0
        let b = Double(rgb & 0xFF) / 255.0
        self = Color(.sRGB, red: r, green: g, blue: b, opacity: 1.0)
    }

    /// Convenience that accepts either 0xAARRGGBB or 0xRRGGBB.
    /// If the value is greater than 0xFFFFFF, it's treated as ARGB; otherwise as RGB with full alpha.
    init(hex: UInt64) {
        if hex > 0xFFFFFF {
            self.init(argb: hex)
        } else {
            self.init(rgb: hex)
        }
    }
}
