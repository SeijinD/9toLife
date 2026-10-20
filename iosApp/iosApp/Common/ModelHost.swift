import SwiftUI

/// Creates a screen's model once, when the screen first appears. Building it in the parent's body
/// would create (and throw away) a shared Kotlin ViewModel on every parent update.
struct ModelHost<Model: AnyObject, Content: View>: View {
    let makeModel: () -> Model
    @ViewBuilder let content: (Model) -> Content

    @State private var model: Model?

    var body: some View {
        if let model {
            content(model)
        } else {
            Color.clear.onAppear { model = makeModel() }
        }
    }
}
