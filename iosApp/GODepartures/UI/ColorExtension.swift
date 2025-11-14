//
//  ColorExtension.swift
//  iosApp
//
//  Created by Jason Bromfield on 2025-10-15.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

extension Color {
    /// Normalize a 64-bit value that may contain ARGB in either the low or high 32 bits.
    /// - If low 32 bits are non-zero, use them as 0xAARRGGBB.
    /// - Else if high 32 bits are non-zero, shift them down.
    /// - Else return 0 (transparent).
    private static func normalizedARGB32(from value: UInt64) -> UInt32 {
        let low = UInt32(truncatingIfNeeded: value)
        if low != 0 {
            return low
        }
        let high = UInt32(truncatingIfNeeded: value >> 32)
        return high
    }

    /// Initialize from ARGB 0xAARRGGBB.
    /// Accepts either:
    /// - Low-packed (0x00000000_AARRGGBB)
    /// - High-packed (0xAARRGGBB_00000000) as produced by some Kotlin/Native bridges
    init(argb value: UInt64) {
        let argb = Color.normalizedARGB32(from: value)
        let a = Double((argb >> 24) & 0xFF) / 255.0
        let r = Double((argb >> 16) & 0xFF) / 255.0
        let g = Double((argb >> 8) & 0xFF) / 255.0
        let b = Double(argb & 0xFF) / 255.0
        self = Color(.sRGB, red: r, green: g, blue: b, opacity: a)
    }

    /// Initialize from RGB 0xRRGGBB (assumes full opacity)
    init(rgb: UInt64) {
        let rgb32 = UInt32(truncatingIfNeeded: rgb)
        let r = Double((rgb32 >> 16) & 0xFF) / 255.0
        let g = Double((rgb32 >> 8) & 0xFF) / 255.0
        let b = Double(rgb32 & 0xFF) / 255.0
        self = Color(.sRGB, red: r, green: g, blue: b, opacity: 1.0)
    }

    /// Convenience that accepts either 0xAARRGGBB or 0xRRGGBB.
    /// If the normalized 32-bit value has a non-zero alpha, treat as ARGB; otherwise as RGB with full alpha.
    init(hex: UInt64) {
        let argb = Color.normalizedARGB32(from: hex)
        let alpha = (argb >> 24) & 0xFF
        if alpha != 0 {
            self.init(argb: UInt64(argb))
        } else {
            self.init(rgb: UInt64(argb))
        }
    }
}
