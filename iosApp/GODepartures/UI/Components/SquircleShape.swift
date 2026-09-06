import SwiftUI

/// A fixed-exponent superellipse (“squircle”) Shape using Euler’s constant `e` as the exponent.
/// The curve follows |x/a|^e + |y/b|^e = 1, scaled to fit the provided rect.
public struct SquircleShape: Shape {

    // Fixed exponent = e (Euler’s constant).
    private let n: CGFloat = CGFloat(M_E)

    // Number of samples for one quadrant. Mirrored to complete the path.
    // 32 is enough for small icons; increase for larger sizes if needed.
    private let quadrantSamples: Int = 48

    public init() {}

    public func path(in rect: CGRect) -> Path {
        // Handle degenerate rects
        guard rect.width > 0, rect.height > 0 else { return Path() }

        // Semi-axes for the superellipse centered in rect
        let a = rect.width / 2
        let b = rect.height / 2
        let center = CGPoint(x: rect.midX, y: rect.midY)

        // Parameterize with angle t in [0, π/2] for the first quadrant
        // and mirror to other quadrants for a closed path.
        var points: [CGPoint] = []
        points.reserveCapacity(quadrantSamples * 4 + 1)

        // Sample first quadrant including endpoints
        for i in 0...quadrantSamples {
            let t = CGFloat(i) / CGFloat(quadrantSamples) * (.pi / 2)
            let cosT = cos(t)
            let sinT = sin(t)

            // Superellipse parametric form:
            // x = a * sign(cos t) * |cos t|^(2/n)
            // y = b * sign(sin t) * |sin t|^(2/n)
            // In first quadrant, signs are positive.
            let x = a * pow(abs(cosT), 2.0 / n)
            let y = b * pow(abs(sinT), 2.0 / n)

            points.append(CGPoint(x: center.x + x, y: center.y + y))
        }

        // Mirror to complete full shape in clockwise order:
        // Q1 (0..π/2), Q2 (π/2..π), Q3 (π..3π/2), Q4 (3π/2..2π)
        // We already have Q1 from (a,0) to (0,b) in increasing y.
        // Build the rest by reflecting across axes.

        // Q2: reflect Q1 across y-axis (x -> -x), reverse to maintain continuity
        let q2 = points.dropFirst().dropLast().reversed().map { p in
            CGPoint(x: 2 * center.x - p.x, y: p.y)
        }

        // Q3: reflect Q1 across both axes (x -> -x, y -> -y)
        let q3 = points.map { p in
            CGPoint(x: 2 * center.x - p.x, y: 2 * center.y - p.y)
        }

        // Q4: reflect Q1 across x-axis (y -> -y), reverse to maintain continuity
        let q4 = points.dropFirst().dropLast().reversed().map { p in
            CGPoint(x: p.x, y: 2 * center.y - p.y)
        }

        var path = Path()
        // Start at the first point of Q1 (rightmost point on the shape)
        if let start = points.first {
            path.move(to: start)
        }

        // Append Q1 (excluding the first point, already moved to)
        for p in points.dropFirst() { path.addLine(to: p) }
        // Append Q2, Q3, Q4
        for p in q2 { path.addLine(to: p) }
        for p in q3 { path.addLine(to: p) }
        for p in q4 { path.addLine(to: p) }

        path.closeSubpath()
        return path
    }
}
