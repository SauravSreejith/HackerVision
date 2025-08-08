package com.hackathon.hackervision

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoaderCallback
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

class MainActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {

    companion object {
        private const val TAG = "HackerVision"
        private const val CAMERA_PERMISSION_REQUEST = 200

        init {
            if (!OpenCVLoaderCallback.initDebug()) {
                Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization")
            } else {
                Log.d(TAG, "OpenCV library found inside package. Using it!")
            }
        }
    }

    private lateinit var cameraView: CameraBridgeViewBase
    private lateinit var scanDepthSeekBar: SeekBar
    private lateinit var scanDepthLabel: TextView

    // Canny edge detection parameters
    private var lowThreshold = 50.0
    private var highThreshold = 150.0

    // OpenCV matrices
    private lateinit var rgbaMat: Mat
    private lateinit var grayMat: Mat
    private lateinit var cannyMat: Mat
    private lateinit var displayMat: Mat

    private val loaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                    Log.d(TAG, "OpenCV loaded successfully")
                    cameraView.enableView()
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep screen on and hide navigation
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        // Initialize views
        cameraView = findViewById(R.id.camera_view)
        scanDepthSeekBar = findViewById(R.id.scan_depth_seekbar)
        scanDepthLabel = findViewById(R.id.scan_depth_label)

        // Setup camera view
        cameraView.visibility = SurfaceView.VISIBLE
        cameraView.setCvCameraViewListener(this)

        // Setup seek bar for scan depth control
        setupScanDepthControl()

        // Check camera permission
        if (checkCameraPermission()) {
            enableCamera()
        } else {
            requestCameraPermission()
        }
    }

    private fun setupScanDepthControl() {
        scanDepthSeekBar.max = 100
        scanDepthSeekBar.progress = 33 // Start at medium sensitivity

        scanDepthSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateScanDepth(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Set initial values
        updateScanDepth(33)
    }

    private fun updateScanDepth(progress: Int) {
        // Map seekbar progress (0-100) to Canny thresholds
        val sensitivity = progress / 100.0

        // Invert the relationship for more intuitive control
        lowThreshold = 20.0 + (180.0 * (1.0 - sensitivity))
        highThreshold = lowThreshold * 2.5

        // Update label
        val detailLevel = when {
            progress < 25 -> "ARCHITECTURAL"
            progress < 50 -> "STRUCTURAL"
            progress < 75 -> "DETAILED"
            else -> "MAXIMUM"
        }
        scanDepthLabel.text = "SCAN DEPTH: $detailLevel"
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST
        )
    }

    private fun enableCamera() {
        if (::cameraView.isInitialized) {
            cameraView.setCameraPermissionGranted()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableCamera()
            } else {
                Toast.makeText(this, "Camera permission required for HackerVision", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!OpenCVLoaderCallback.initDebug()) {
            OpenCVLoaderCallback.initAsync(OpenCVLoaderCallback.OPENCV_VERSION, this, loaderCallback)
        } else {
            loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    override fun onPause() {
        super.onPause()
        if (::cameraView.isInitialized) {
            cameraView.disableView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::cameraView.isInitialized) {
            cameraView.disableView()
        }
    }

    // CameraBridgeViewBase.CvCameraViewListener2 implementations

    override fun onCameraViewStarted(width: Int, height: Int) {
        rgbaMat = Mat(height, width, CvType.CV_8UC4)
        grayMat = Mat(height, width, CvType.CV_8UC1)
        cannyMat = Mat(height, width, CvType.CV_8UC1)
        displayMat = Mat(height, width, CvType.CV_8UC4)
    }

    override fun onCameraViewStopped() {
        if (::rgbaMat.isInitialized) rgbaMat.release()
        if (::grayMat.isInitialized) grayMat.release()
        if (::cannyMat.isInitialized) cannyMat.release()
        if (::displayMat.isInitialized) displayMat.release()
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        rgbaMat = inputFrame.rgba()

        // Convert to grayscale for Canny edge detection
        Imgproc.cvtColor(rgbaMat, grayMat, Imgproc.COLOR_RGBA2GRAY)

        // Apply Gaussian blur to reduce noise
        Imgproc.GaussianBlur(grayMat, grayMat, org.opencv.core.Size(3.0, 3.0), 0.0)

        // Apply Canny edge detection with current thresholds
        Imgproc.Canny(grayMat, cannyMat, lowThreshold, highThreshold)

        // Create the HackerVision effect
        createHackerVisionEffect()

        return displayMat
    }

    private fun createHackerVisionEffect() {
        // Create black canvas (void background)
        displayMat.setTo(Scalar(0.0, 0.0, 0.0, 255.0))

        // Convert single-channel Canny result to RGBA
        val cannyRgba = Mat()
        Imgproc.cvtColor(cannyMat, cannyRgba, Imgproc.COLOR_GRAY2RGBA)

        // Create phosphor green mask where edges exist
        val mask = Mat()
        Core.inRange(cannyRgba, Scalar(255.0, 255.0, 255.0, 255.0), Scalar(255.0, 255.0, 255.0, 255.0), mask)

        // Apply phosphor green color (0, 255, 0) to edge pixels
        displayMat.setTo(Scalar(0.0, 255.0, 0.0, 255.0), mask)

        // Add subtle scanline effect for retro monitor feel
        addScanlineEffect()

        // Clean up
        cannyRgba.release()
        mask.release()
    }

    private fun addScanlineEffect() {
        // Add subtle horizontal scanlines every 4 pixels
        val rows = displayMat.rows()
        val scanlineMat = Mat.zeros(displayMat.size(), displayMat.type())

        for (y in 0 until rows step 4) {
            if (y < rows) {
                val roi = scanlineMat.submat(y, y + 1, 0, displayMat.cols())
                roi.setTo(Scalar(0.0, 80.0, 0.0, 30.0)) // Very subtle green scanlines
                roi.release()
            }
        }

        // Blend scanlines with the main image
        Core.add(displayMat, scanlineMat, displayMat)
        scanlineMat.release()
    }
}