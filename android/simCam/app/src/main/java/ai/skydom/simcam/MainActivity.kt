package ai.skydom.simcam

import android.os.Bundle
import androidx.activity.ComponentActivity

import java.nio.ByteBuffer
// components
import android.widget.Button
import android.widget.ImageView
//permissions
//import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
//camera
import androidx.camera.core.ImageAnalysis
import android.Manifest
import android.content.Context
import androidx.camera.view.PreviewView
//convert to image
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager

import android.util.Log
import android.util.Size
import androidx.camera.core.AspectRatio

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider


//thread
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


private lateinit var cameraExecutor: ExecutorService



class MainActivity : ComponentActivity() {
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraPreview: PreviewView
    private lateinit var processedImageView: ImageView
    private lateinit var captureButton: Button
    private lateinit var resetButton: Button
    private lateinit var imageAnalysis: ImageAnalysis
    private var processNextImage = false

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraPreview = findViewById(R.id.camera_preview)
        processedImageView = findViewById(R.id.processed_image)
        captureButton = findViewById(R.id.button_capture)
        resetButton = findViewById(R.id.button_reset)
        cameraExecutor = Executors.newSingleThreadExecutor()

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
        // enableEdgeToEdge()
        // set the aspect ratio
        val sizes = findCameraResolutionCamera2(this)
//        val aspectRatio = getAspectRatio(camera.cameraInfo.sensorRotationDegrees, cameraPreview.width, cameraPreview.height)
//        Log.d("CameraSetup", "Aspect Ratio - $aspectRatio:1")
        // Check if permissions are already granted, if not, request them
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            startCamera()
        }
    }



    fun findCameraResolutionCamera2(context: Context): Array<Size> {
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val outputSizes = mutableListOf<Size>()
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)
                val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                map?.getOutputSizes(ImageFormat.JPEG)?.let {
                    outputSizes.addAll(it)
                    it.forEach { size ->
                        Log.i("CameraSetup", "Available resolution: ${size.width}x${size.height}")
                    }
                }
            }
        } catch (e: CameraAccessException) {
            Log.e("CameraSetup", "Exception occurred: ${e.message}")
        }
        return outputSizes.toTypedArray()
    }
    private fun updatePreviewAspectRatio(width: Int, height: Int) {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        val screenRatio = cameraPreview.width.toDouble() / cameraPreview.height

        val newWidth: Int
        val newHeight: Int
        if (previewRatio > screenRatio) {
            newWidth = cameraPreview.width
            newHeight = (cameraPreview.width / previewRatio).toInt()
        } else {
            newHeight = cameraPreview.height
            newWidth = (cameraPreview.height * previewRatio).toInt()
        }

        val layoutParams = cameraPreview.layoutParams
        layoutParams.width = newWidth
        layoutParams.height = newHeight
        cameraPreview.layoutParams = layoutParams
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            cameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val aspectRatio = AspectRatio.RATIO_4_3 // Or RATIO_16_9 if your camera supports it
            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy(aspectRatio, AspectRatioStrategy.FALLBACK_RULE_AUTO))
                .build()
            val resolutionSelectorRes =ResolutionSelector.Builder()
                .setResolutionStrategy(
                    ResolutionStrategy(
                        Size(1280, 720),
                        ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                    )
                )
                .build()
            val preview = Preview.Builder()
                .setResolutionSelector(resolutionSelector)
                .build()
                .also {
                    it.setSurfaceProvider(cameraPreview.surfaceProvider)
                }
            setupImageAnalysis(resolutionSelector) // Configure image analysis
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                val camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis
                )


                // Get the camera's sensor orientation and resolution
                val cameraId = camera.cameraInfo.cameraSelector.toString().substringAfter("PRIMARY")
                val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0
                val sensorSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)

                if (sensorSize != null) {
                    runOnUiThread {
                        if (sensorOrientation == 90 || sensorOrientation == 270) {
                            updatePreviewAspectRatio(sensorSize.height(), sensorSize.width())
                        } else {
                            updatePreviewAspectRatio(sensorSize.width(), sensorSize.height())
                        }
                    }
                }
            } catch (exc: Exception) {
                // Handle exceptions, e.g., camera binding failed
                Log.e("CameraXApp", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureAndProcessImage() {
        // Implement functionality to capture and process the image here
        // This could invoke the camera capture, process the image, and then display it
        Toast.makeText(this, "Capture button clicked!", Toast.LENGTH_SHORT).show()
        processNextImage = true
    }

    private fun setupImageAnalysis(resolutionSelector: ResolutionSelector) {
        imageAnalysis = ImageAnalysis.Builder()
            .setResolutionSelector(resolutionSelector)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888) // Assuming RGBA output
            .build()
            .also { analysis ->
                analysis.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { image ->
                    try {
                        val planes = image.planes
                        val buffer = planes[0].buffer
                        buffer.rewind()  // Ensure the buffer is at the beginning
                        if (processNextImage) {
                            runOnUiThread {
                                val rgbaBuffer  = NativeLib().procimage(buffer, image.imageInfo.rotationDegrees, image.width, image.height)
                                logPixelValues(rgbaBuffer, image.width, image.height)
                                val rotatedBitmap = rotateBitmapFromBuffer(rgbaBuffer, image.width, image.height, image.imageInfo.rotationDegrees)
//                                buffer.rewind()
//                                val bitmap = Bitmap.createBitmap(image.height, image.width, Bitmap.Config.ARGB_8888)
//                                bitmap.copyPixelsFromBuffer(rgbaBuffer)
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

// Define a class that encapsulates native methods
class NativeLib {
    // Load the native library where the native methods are implemented
    init {
        System.loadLibrary("imgproc")
    }

    // Declare a native method
    external fun procimage(input: ByteBuffer, rotation: Int, width: Int, height: Int): ByteBuffer
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

fun logPixelValues(buffer: ByteBuffer, width: Int, height: Int) {
    val positions = listOf(Pair(0, 0), Pair(10, 10), Pair(width - 1, height - 1))  // Example positions
    buffer.rewind()  // Ensure the buffer is at the start

    for ((x, y) in positions) {
        val index = (y * width + x) * 4  // Calculate the byte index for pixel (x, y), assuming RGBA format
        if (index < buffer.limit() - 4) {  // Ensure the index is within the buffer limit
            val r = buffer.get(index + 0).toInt() and 0xFF
            val g = buffer.get(index + 1).toInt() and 0xFF
            val b = buffer.get(index + 2).toInt() and 0xFF
            val a = buffer.get(index + 3).toInt() and 0xFF  // Alpha is usually the first byte in RGBA
            Log.d("PixelValues", "Pixel at ($x, $y): RGBA($r, $g, $b, $a)")
        } else {
            Log.d("PixelValues", "Pixel at ($x, $y) is out of bounds")
        }
    }

    buffer.rewind()  // Optionally rewind the buffer if needed elsewhere
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



