//
//  TrainListItem.swift
//  iosApp
//
//  Created by Jason Bromfield on 2024-12-27.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import shared

struct TrainListItem: View {
    var train: Train
    init(train: Train) {
        self.train = train
    }
    var body: some View {
        HStack(spacing: 10) {
            Text(train.code)
                .padding(.all, 10)
                .fontWeight(.bold)
                .foregroundColor(.white)
                .background(Circle().fill(Color(hex: train.color)))
            Text(train.destination)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.horizontal, 10)
            VStack {
                Text(train.platform).fontWeight(.bold)
                Text(train.departureTime)
            }
        }.frame(maxWidth: .infinity)
    }
}

#Preview {
    TrainListItem(
        train: Train.init(
            code: "RH",
            name: "Richmond Hill",
            destination: "Bloomington GO",
            platform: "4 & 5",
            departureTime: "12:34",
            color: 0x45AAFF,
            tripOrder: 0)
    )
}
extension Color {
    init(hex: Int32, opacity: Double = 1) {
        self.init(
            .sRGB,
            red: Double((hex >> 16) & 0xff) / 255,
            green: Double((hex >> 08) & 0xff) / 255,
            blue: Double((hex >> 00) & 0xff) / 255,
            opacity: opacity
        )
    }
}
