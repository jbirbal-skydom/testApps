//
//  ContentView.swift
//  simmath
//
//  Created by Skydom on 6/25/24.
//

import SwiftUI

struct ContentView: View {
    @State private var number1: String = ""
    @State private var number2: String = ""
    @State private var result: Int?

    var body: some View {
        VStack(spacing: 16) {
            TextField("Enter number 1", text: $number1)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding(.horizontal)
                .keyboardType(.numberPad)
            
            TextField("Enter number 2", text: $number2)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding(.horizontal)
                .keyboardType(.numberPad)
            
            Button(action: {
                if let num1 = Int(number1), let num2 = Int(number2) {
                    result = NativeLib().addNumbers(a: num1, b: num2)
                }
            }) {
                Text("Add Numbers")
            }
            .padding(.top, 8)
            
            if let result = result {
                Text("Result: \(result)")
                    .padding(.top, 8)
            }
        }
        .padding()
    }
}

class NativeLib {
    func addNumbers(a: Int, b: Int) -> Int {
        // Call the Rust function here.
        // This requires setting up Rust to work with Swift.
        // For example, you might use FFI to call a Rust function.
        return add_numbers(a, b)
    }
}

#Preview {
    ContentView()
}
