import SwiftUI
import shared

struct ContentView: View {
    @ObservedObject private(set) var viewModel: ViewModel

    var body: some View {
        VStack {
            Text("Departure Screen")
                .fontWeight(.bold)
            List {
                ForEach(viewModel.trains, id: \.self) { train in
                    TrainListItem(train: train)
                }
            }
        }
    }
}

extension ContentView {
    class ViewModel: ObservableObject {
        @Published var trains: [Train] = []
        init() {
            GoTrainDataSource().getTrains() { trains, error in
                DispatchQueue.main.async {
                    if let trains = trains {
                        self.trains = trains
                    } else {
                        self.trains = []
                    }
                }
            }
        }
    }
}

// struct ContentView_Previews: PreviewProvider {
//     static var previews: some View {
//         ContentView()
//     }
// }
