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
import android.content.ContentValues
import android.content.Context
import androidx.camera.view.PreviewView
//convert to image
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
//import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import java.io.FileOutputStream
import java.nio.ByteOrder

//thread
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.*
import kotlin.math.min


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
    private lateinit var resetButton: Button
    private lateinit var imageAnalysis: ImageAnalysis

    private fun setupImageAnalysis() {
        imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888) // Assuming RGBA output
            .build()
            .also { analysis ->
                analysis.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { image ->
                    try {
                        val planes = image.planes
                        val buffer = planes[0].buffer
                        buffer.rewind()  // Ensure the buffer is at the beginning
                        val rotatedBitmap = rotateBitmapFromBuffer(buffer, image.width, image.height, image.imageInfo.rotationDegrees)

                        if (processNextImage) {
                            runOnUiThread {
                                processedImageView.setImageBitmap(rotatedBitmap)
                            }
                            processNextImage = false
                        }
                    } catch (e: Exception) {
                        Log.e("ImageAnalysis", "Error processing image", e)
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
        resetButton = findViewById(R.id.button_reset)

        cameraExecutor = Executors.newSingleThreadExecutor()

        // Setting up the button click listener
        setupImageAnalysis()
        captureButton.setOnClickListener {
            // Call a function or perform an action when the button is clicked
            captureAndProcessImage()
        }

        resetButton.setOnClickListener {
            // Assuming you have stored the dimensions of the last processed image or defaulting to some size
            val lastWidth = processedImageView.width.takeIf { it > 0 } ?: 640
            val lastHeight = processedImageView.height.takeIf { it > 0 } ?: 480

            // Set a random bitmap to the processedImageView
            setRandomBitmap(processedImageView, lastWidth, lastHeight)
        }




        enableEdgeToEdge()



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

            setupImageAnalysis() // Configure image analysis



            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis)
            } catch (exc: Exception) {
                // Handle exceptions, e.g., camera binding failed
                Log.e("CameraXApp", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    fun fillPixelsArray(pixels: IntArray, buffer: ByteBuffer) {
        buffer.rewind()
        for (i in pixels.indices) {
            val a = buffer.get().toInt() and 0xFF
            val r = buffer.get().toInt() and 0xFF
            val g = buffer.get().toInt() and 0xFF
            val b = buffer.get().toInt() and 0xFF
            pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }
    }
}




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
    external fun procimage(yBuffer: ByteBuffer, uBuffer: ByteBuffer, vBuffer: ByteBuffer,width: Int, height: Int): ByteBuffer
}


fun logBufferContents(buffer: ByteBuffer) {
    buffer.rewind()  // Ensure the buffer is at the start position

    // Convert part of the ByteBuffer to ByteArray for easy access
    val bytesToShow = 16  // Number of bytes to log
    val array = ByteArray(bytesToShow)
    buffer.get(array, 0, bytesToShow)

    // Convert byte array to a hex string for logging
    val hexString = array.joinToString(separator = " ") { byte ->
        "%02x".format(byte)
    }

    // Log the hex string
    Log.d("BufferLog", "First $bytesToShow bytes: $hexString")

    buffer.rewind()  // Rewind the buffer again if it needs to be used after logging
}


fun setRandomBitmap(processedImageView: ImageView, width: Int, height: Int) {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val random = Random()

    for (x in 0 until width) {
        for (y in 0 until height) {
            // Generate a random ARGB color
            val a = 255  // Full opacity
            val r = random.nextInt(256)  // Red component
            val g = random.nextInt(256)  // Green component
            val b = random.nextInt(256)  // Blue component
            bitmap.setPixel(x, y, (a shl 24) or (r shl 16) or (g shl 8) or b)
        }
    }

    // Update the ImageView on the UI thread
    processedImageView.post {
        processedImageView.setImageBitmap(bitmap)
    }
}

fun saveBitmapToMediaStore(context: Context, bitmap: Bitmap, filename: String) {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }

    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    if (uri != null) {
        val outputStream = context.contentResolver.openOutputStream(uri)
        if (outputStream != null) {
            outputStream.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            Log.d("SaveFile", "File saved to MediaStore at: $uri")
        } else {
            Log.e("SaveFile", "Failed to open output stream")
        }
    } else {
        Log.e("SaveFile", "Failed to insert MediaStore entry")
    }
}

fun logPixelValues(buffer: ByteBuffer, width: Int, height: Int) {
    val positions = listOf(Pair(0, 0), Pair(10, 10), Pair(width - 1, height - 1))  // Example positions
    buffer.rewind()  // Ensure the buffer is at the start

    for ((x, y) in positions) {
        val index = (y * width + x) * 4  // Calculate the byte index for pixel (x, y), assuming RGBA format
        if (index < buffer.limit() - 4) {  // Ensure the index is within the buffer limit
            val r = buffer.get(index + 1).toInt() and 0xFF
            val g = buffer.get(index + 2).toInt() and 0xFF
            val b = buffer.get(index + 3).toInt() and 0xFF
            val a = buffer.get(index).toInt() and 0xFF  // Alpha is usually the first byte in RGBA
            Log.d("PixelValues", "Pixel at ($x, $y): RGBA($r, $g, $b, $a)")
        } else {
            Log.d("PixelValues", "Pixel at ($x, $y) is out of bounds")
        }
    }

    buffer.rewind()  // Optionally rewind the buffer if needed elsewhere
}
fun logBitmapPixels(bitmap: Bitmap) {
    val positions = listOf(
        Pair(0, 0),  // Top-left corner
        Pair(bitmap.width / 2, bitmap.height / 2),  // Center
        Pair(bitmap.width - 1, 0),  // Top-right corner
        Pair(0, bitmap.height - 1),  // Bottom-left corner
        Pair(bitmap.width - 1, bitmap.height - 1)  // Bottom-right corner
    )

    positions.forEach { (x, y) ->
        val pixel = bitmap.getPixel(x, y)
        val a = (pixel shr 24) and 0xff  // Alpha component
        val r = (pixel shr 16) and 0xff  // Red component
        val g = (pixel shr 8) and 0xff   // Green component
        val b = pixel and 0xff          // Blue component
        Log.d("BitmapPixels", "Pixel at ($x, $y): ARGB($a, $r, $g, $b)")
    }
}

fun rotateBitmapFromBuffer(buffer: ByteBuffer, width: Int, height: Int, rotationDegrees: Int): Bitmap {
    buffer.rewind() // Rewind buffer before reading from it
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.copyPixelsFromBuffer(buffer)

    // Creating a new matrix for rotation
    val matrix = Matrix().apply {
        postRotate(rotationDegrees.toFloat())
    }

    // Returning a new bitmap rotated using the matrix
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}