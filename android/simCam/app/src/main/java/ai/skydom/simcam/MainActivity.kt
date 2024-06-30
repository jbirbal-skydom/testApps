package ai.skydom.simcam

import android.os.Bundle
import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import ai.skydom.simcam.ui.theme.SimcamTheme
import java.nio.ByteBuffer
// components
import android.widget.Button
import android.widget.ImageView
//permissions
//import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.widget.Toast
//import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
//camera
import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.ImageProxy
import android.Manifest
import androidx.camera.view.PreviewView
//convert to image
import android.graphics.Bitmap
//import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider

//thread
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


private lateinit var cameraExecutor: ExecutorService


class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, "Camera permission is required to use this feature", Toast.LENGTH_LONG).show()

        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all { permission: String ->
        ContextCompat.checkSelfPermission(baseContext, permission) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private lateinit var cameraPreview: PreviewView
    private lateinit var processedImageView: ImageView
    private lateinit var captureButton: Button
    private lateinit var imageAnalysis: ImageAnalysis

    private fun setupImageAnalysis() {
        imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { image ->
                    val buffer = image.planes[0].buffer
                    val procBuffer = NativeLib().procimage(buffer, image.width, image.height)
                    // val processedBitmap = byteBufferToBitmap(procBuffer, image.width, image.height)
                    runOnUiThread {
                        //processedImageView.setImageBitmap(processedBitmap)
                    }
                    image.close()
                })
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        cameraPreview = findViewById(R.id.camera_preview)
        processedImageView = findViewById(R.id.processed_image)
        captureButton = findViewById(R.id.button_capture)

        cameraExecutor = Executors.newSingleThreadExecutor()

        // Setting up the button click listener
        setupImageAnalysis()
        captureButton.setOnClickListener {
            // Call a function or perform an action when the button is clicked
            captureAndProcessImage()
        }




        enableEdgeToEdge()
//        setContent {
//            SimcamTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }


        // Check if permissions are already granted, if not, request them
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            startCamera()
        }
    }
    private var processNextImage = false
    private fun captureAndProcessImage() {
        // Implement functionality to capture and process the image here
        // This could invoke the camera capture, process the image, and then display it
        Toast.makeText(this, "Capture button clicked!", Toast.LENGTH_SHORT).show()
        processNextImage = true
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(cameraPreview.surfaceProvider)
                }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { image ->
                        if (processNextImage) {
                            val buffer = image.planes[0].buffer
                            val processedBuffer = NativeLib().procimage(buffer, image.width, image.height)
                            //val processedBitmap = byteBufferToBitmap(processedBuffer, image.width, image.height)
                            runOnUiThread {
                                // processedImageView.setImageBitmap(processedBitmap)
                            }
                            processNextImage = false
                        }
                        image.close()
                    })


//                    it.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { image ->
//                        // Image processing logic here
//                        val buffer = image.planes[0].buffer
//                        val processedBuffer = NativeLib().procimage(buffer, image.width, image.height)
//                        //val processedBitmap = byteBufferToBitmap(processedBuffer, image.width, image.height)
//
//                        runOnUiThread {
//                            //processedImageView.setImageBitmap(processedBitmap)
//                        }
//                        image.close()
//                    })
                }

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis)
            } catch (exc: Exception) {
                // Handle exceptions, e.g., camera binding failed
            }
        }, ContextCompat.getMainExecutor(this))
    }

//    private fun startCamera() {
//
//
//        val imageAnalysis = ImageAnalysis.Builder()
//            .build()
//            .also {
//                it.setAnalyzer(ContextCompat.getMainExecutor(this), ImageAnalysis.Analyzer { image ->
//                    // Image processing logic here
//                    val buffer = image.planes[0].buffer
//                    val width = image.width
//                    val height = image.height
//                    val processedBuffer =  NativeLib().procimage(buffer, width, height)
//                    val processedBitmap = byteBufferToBitmap(processedBuffer, width, height)
//                    // Convert processedBuffer back to image and set to ImageView
//                    runOnUiThread {
//                        // Update your ImageView with the new image
//                        processedImageView.setImageBitmap(processedBitmap)
//                    }
//                    image.close()
//                })
//            }
//
//        // Bind the lifecycle of cameras to the lifecycle owner
//        // CameraX.bindToLifecycle(this as LifecycleOwner, imageAnalysis)
//    }
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
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
//    SimcamTheme {
//        Greeting("Android")
//    }
//}


fun byteBufferToBitmap(buffer: ByteBuffer, width: Int, height: Int): Bitmap {
    buffer.rewind()  // Rewind the buffer to start reading from the first byte
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val pixels = IntArray(width * height)

    // Assuming the buffer is in RGB format
    for (i in 0 until width * height) {
        val r = buffer.get().toInt() and 0xFF
        val g = buffer.get().toInt() and 0xFF
        val b = buffer.get().toInt() and 0xFF
        pixels[i] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b  // Convert to ARGB
    }

    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}




// Define a class that encapsulates native methods
class NativeLib {
    // Load the native library where the native methods are implemented
    init {
        System.loadLibrary("imgproc")
    }

    // Declare a native method
    external fun procimage(input: ByteBuffer, width: Int, height: Int)//: ByteBuffer
}


