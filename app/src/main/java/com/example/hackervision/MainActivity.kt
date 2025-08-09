// app/src/main/java/com/hackathon/hackervision/MainActivity.kt

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
// The import for BaseLoaderCallback is no longer needed
// import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
// The import for LoaderCallbackInterface is no longer needed
// import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

class MainActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {

    companion object {
        private const val TAG = "HackerVision"
        private const val CAMERA_PERMISSION_REQUEST = 200

        // This static block is now the ONLY thing needed for initialization.
        // It's executed once when the class is loaded.
        init {
            if (OpenCVLoader.initDebug()) {
                Log.d(TAG, "OpenCV library found inside package. Using it!")
            } else {
                Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization")
                // Note: The 'else' block will likely not be reached if your :opencv module is set up correctly.
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

    // --- REMOVED ---
    // The BaseLoaderCallback is no longer necessary with static initialization.
    /*
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
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)

        cameraView = findViewById(R.id.camera_view)
        scanDepthSeekBar = findViewById(R.id.scan_depth_seekbar)
        scanDepthLabel = findViewById(R.id.scan_depth_label)

        cameraView.visibility = SurfaceView.VISIBLE
        cameraView.setCvCameraViewListener(this)

        setupScanDepthControl()

        if (checkCameraPermission()) {
            // Since OpenCV is loaded statically, we can enable the view directly
            // after the permission check.
            cameraView.enableView()
        } else {
            requestCameraPermission()
        }
    }

    private fun setupScanDepthControl() {
        scanDepthSeekBar.max = 100
        scanDepthSeekBar.progress = 33

        scanDepthSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateScanDepth(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        updateScanDepth(33)
    }

    private fun updateScanDepth(progress: Int) {
        val sensitivity = progress / 100.0
        lowThreshold = 20.0 + (180.0 * (1.0 - sensitivity))
        highThreshold = lowThreshold * 2.5
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
            // Enable the view here after permission is granted.
            cameraView.enableView()
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

    // --- MODIFIED ---
    // The onResume method is simplified. We no longer need to call initAsync.
    // The camera view is enabled here to ensure it's active when the app comes to the foreground.
    override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "OpenCV failed to load!")
        } else {
            Log.d(TAG, "OpenCV library found, attempting to enable camera view.")
            if (checkCameraPermission()) {
                // Be explicit about which camera to use
                cameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK)
                cameraView.setMaxFrameSize(1280, 720)
                cameraView.enableView()
            }
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

    // CameraBridgeViewBase.CvCameraViewListener2 implementations (NO CHANGES HERE)
    // CameraBridgeViewBase.CvCameraViewListener2 implementations

    override fun onCameraViewStarted(width: Int, height: Int) {
        Log.d(TAG, "SUCCESS: onCameraViewStarted is called. Frame size: ${width}x${height}")
        rgbaMat = Mat(height, width, CvType.CV_8UC4)
        grayMat = Mat(height, width, CvType.CV_8UC1)
        cannyMat = Mat(height, width, CvType.CV_8UC1)
        displayMat = Mat(height, width, CvType.CV_8UC4)
    }

    override fun onCameraViewStopped() {
        Log.d(TAG, "onCameraViewStopped is called.")
        if (::rgbaMat.isInitialized) rgbaMat.release()
        if (::grayMat.isInitialized) grayMat.release()
        if (::cannyMat.isInitialized) cannyMat.release()
        if (::displayMat.isInitialized) displayMat.release()
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        // Get the current camera frame
        rgbaMat = inputFrame.rgba()

        // Convert to grayscale for edge detection
        Imgproc.cvtColor(rgbaMat, grayMat, Imgproc.COLOR_RGBA2GRAY)

        // Apply a slight blur to reduce noise
        Imgproc.GaussianBlur(grayMat, grayMat, org.opencv.core.Size(3.0, 3.0), 0.0)

        // Perform Canny edge detection
        Imgproc.Canny(grayMat, cannyMat, lowThreshold, highThreshold)

        // Use the edge data to create the final visual effect
        createHackerVisionEffect()

        // Return the final, modified frame to be displayed on screen
        return displayMat
    }

    private fun createHackerVisionEffect() {
        displayMat.setTo(Scalar(0.0, 0.0, 0.0, 255.0))
        val cannyRgba = Mat()
        Imgproc.cvtColor(cannyMat, cannyRgba, Imgproc.COLOR_GRAY2RGBA)
        val mask = Mat()
        Core.inRange(cannyRgba, Scalar(255.0, 255.0, 255.0, 255.0), Scalar(255.0, 255.0, 255.0, 255.0), mask)
        displayMat.setTo(Scalar(0.0, 255.0, 0.0, 255.0), mask)
        addScanlineEffect()
        cannyRgba.release()
        mask.release()
    }

    private fun addScanlineEffect() {
        val rows = displayMat.rows()
        val scanlineMat = Mat.zeros(displayMat.size(), displayMat.type())
        for (y in 0 until rows step 4) {
            if (y < rows) {
                val roi = scanlineMat.submat(y, y + 1, 0, displayMat.cols())
                roi.setTo(Scalar(0.0, 80.0, 0.0, 30.0))
                roi.release()
            }
        }
        Core.add(displayMat, scanlineMat, displayMat)
        scanlineMat.release()
    }
}