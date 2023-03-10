package com.glaukous.views.scanner

import android.Manifest.permission.CAMERA
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.glaukous.MainActivity
import com.glaukous.R
import com.glaukous.databinding.ScannerBinding
import com.glaukous.extensions.showToast
import com.glaukous.utils.PermissionUtils.hasPermissions
import com.glaukous.utils.PermissionUtils.isPermissionsGranted
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class Scanner : Fragment() {
    private var _binding: ScannerBinding? = null
    private var binding = _binding
    private lateinit var cameraExecutor: ExecutorService
    private val viewModel by viewModels<ScannerVM>()
    private val args by navArgs<ScannerArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = ScannerBinding.inflate(layoutInflater, container, false)
        binding?.viewModel = viewModel
        requestPermission()

        return binding?.root
    }

    private fun requestPermission() {

        if (hasPermissions(context = requireContext(), permissions = arrayOf(CAMERA))) {
            // Permission granted: start the preview
            startCamera()
        } else {
            // Permission denied
            requestCameraPermission.launch(arrayOf(CAMERA))
        }
    }

    private fun startCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding?.previewView?.surfaceProvider)
            }

            // Image analyzer
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                //.setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
                //.setImageQueueDepth(1)
                .build().also { it ->
                    it.setAnalyzer(
                        cameraExecutor,
                        QrCodeAnalyzer(onBarCodeScannerSuccess = { data ->
                            val quantity = 3.takeIf {
                                data.trim().startsWith("NBR") || data.trim().startsWith(
                                    "IBR"
                                )
                            } ?: 1
                            //it.clearAnalyzer()
                            //cameraProvider.unbindAll()
                            Log.e("Scanner", "Scanner Success: Data -> $data")

                            if (data.isNotEmpty()) {
                                cameraExecutor.shutdown()
                                if (args.barcode!=null){
                                    if (args.barcode.equals(data.trim(),true)){
                                        binding?.root?.let { it1 ->
                                            viewModel.navigateToInput(
                                                it1,
                                                barcode = data.trim(),
                                                quantity = quantity+args.quantity
                                            )
                                        }
                                    }else{
                                        "Code doesn't match".showToast()
                                        binding?.root?.let { it1 ->
                                            viewModel.navigateToInput(
                                                it1,
                                                barcode = args.barcode!!.trim(),
                                                quantity = args.quantity
                                            )
                                        }
                                    }
                                }else{
                                    binding?.root?.let { it1 ->
                                        viewModel.navigateToInput(
                                            it1,
                                            barcode = data.trim(),
                                            quantity = quantity+args.quantity
                                        )
                                    }
                                }
                            }
                        }, onBarCodeScannerFailed = { exception ->
                            Log.e("Scanner", "Scanner Failed: ${exception.message}")
                            exception.message?.showToast()
                        }),
                    )
                }

            // Select back camera as a default
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalyzer
                )

            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
        }
    }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (isPermissionsGranted(requireContext(), it)) {
                startCamera()
            } else {
                MaterialAlertDialogBuilder(requireContext()).setTitle("Permission required")
                    .setMessage(requireContext().getString(R.string.require_permission_msg))
                    .setPositiveButton("Ok") { _, _ ->
                        // Keep asking for permission until granted
                        requestPermission()
                    }.setCancelable(false).create().apply {
                        setCanceledOnTouchOutside(false)
                        show()
                    }

            }
        }
}