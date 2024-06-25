package ai.skydom.rust_template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.text.BasicTextField
//import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ai.skydom.rust_template.ui.theme.Rust_templateTheme
//import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
//import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Column
//import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import android.util.Log


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load Rust library
//        System.loadLibrary("simple_math")
        // Logging before loading the library
        Log.d("LibraryLoader", "Attempting to load native library...")

        try {
            System.loadLibrary("simple_math")
            // Logging after successful loading
            Log.d("LibraryLoader", "Successfully loaded native library.")
        } catch (e: UnsatisfiedLinkError) {
            // Logging failed loading
            Log.e("LibraryLoader", "Failed to load native library.", e)
        }

        try {
            val result = NativeLib().addNumbers(5, 3)
            Log.d("LibraryUsage", "Result of addNumbers: $result")
        } catch (e: Exception) {
            Log.e("LibraryUsage", "Error calling native method: ", e)
        }

        enableEdgeToEdge()
        setContent {
            Rust_templateTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding: PaddingValues ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
                MyApp()
            }
        }
    }
}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    Rust_templateTheme {
//        Greeting("Android")
//    }
//}

@Composable
fun MyApp() {
    var number1 by remember { mutableStateOf("") }
    var number2 by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = number1,
            onValueChange = { number1 = it },
            label = { Text("Enter number 1") }
        )

        OutlinedTextField(
            value = number2,
            onValueChange = { number2 = it },
            label = { Text("Enter number 2") }
        )

        Button(onClick = {
            val num1 = number1.toIntOrNull()
            val num2 = number2.toIntOrNull()
            if (num1 != null && num2 != null) {
                result = NativeLib().addNumbers(num1, num2)
            }
        }) {
            Text("Add Numbers")
        }

        result?.let {
            Text("Result: $it", modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Rust_templateTheme {
        MyApp()
    }
}

class NativeLib {
    external fun addNumbers(a: Int, b: Int): Int
}