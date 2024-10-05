package com.dicoding.geotaggingjbg.ui.camera

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dicoding.geotaggingjbg.databinding.FragmentCameraBinding
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.Build
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.dicoding.geotaggingjbg.R
import com.dicoding.geotaggingjbg.data.database.Entity
import com.dicoding.geotaggingjbg.data.database.RemoteEntity
import com.dicoding.geotaggingjbg.ui.detail.DetailFragment
import com.dicoding.geotaggingjbg.ui.detailremote.DetailRemoteFragment
import com.dicoding.geotaggingjbg.ui.save.SaveFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class CameraFragment : Fragment() {
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var codeScanner: CodeScanner
    private var _locationBundle: Bundle? = null

    private lateinit var resultId: String

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireActivity(), "Permission request granted", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(requireActivity(), "Permission request denied", Toast.LENGTH_LONG)
                    .show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireActivity(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        takePhoto()
        binding.ivCamClose.setOnClickListener {
            it.findNavController().navigate(R.id.action_navigation_camera_to_navigation_home)
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI(requireActivity())
    }

    private fun takePhoto() {
        if (allPermissionsGranted()) {
            getLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getLocation() {
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        val elevation = location.altitude
                        scanQRCode(latitude, longitude, elevation)
                    } else {
                        showToast("Gagal mendapatkan lokasi. Menggunakan data lokasi terakhir diketahui.")
                        val lastKnownLocation = getLastKnownLocationFromCache()
                        if (lastKnownLocation != null) {
                            scanQRCode(
                                lastKnownLocation.latitude,
                                lastKnownLocation.longitude,
                                lastKnownLocation.altitude
                            )
                        } else {
                            showToast("Gagal mendapatkan lokasi")
                        }
                    }
                }
                .addOnFailureListener { e ->
                    showToast("Failed to retrieve location: ${e.message}")
                    val lastKnownLocation = getLastKnownLocationFromCache()
                    if (lastKnownLocation != null) {
                        scanQRCode(
                            lastKnownLocation.latitude,
                            lastKnownLocation.longitude,
                            lastKnownLocation.altitude
                        )
                    } else {
                        showToast("Failed to retrieve last known location")
                    }
                }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun scanQRCode(latitude: Double, longitude: Double, elevation: Double) {
        codeScanner = CodeScanner(requireContext(), binding.qrScannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            requireActivity().runOnUiThread {
                val bundle = Bundle().apply {
                    putDouble("latitude", latitude)
                    putDouble("longitude", longitude)
                    putDouble("elevation", elevation)
                }
                _locationBundle = bundle
                handleScanResult(it.text)
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Aplikasi memerlukan akses camera", Toast.LENGTH_LONG).show()
            }
        }
        codeScanner.startPreview()
    }

    private fun handleScanResult(contents: String) {
        resultId = contents
        showToast("QR Code: $contents")
        val bundle = Bundle().apply {
            putString("SCANNED_DATA", contents)
            putAll(_locationBundle)
        }
        showFragment(bundle)
    }

    private fun getLastKnownLocationFromCache(): Location? {
        val sharedPreferences = requireContext().getSharedPreferences("LocationCache", Context.MODE_PRIVATE)
        val latitude = sharedPreferences.getFloat("lastLatitude", 0.0f).toDouble()
        val longitude = sharedPreferences.getFloat("lastLongitude", 0.0f).toDouble()
        val elevation = sharedPreferences.getFloat("lastElevation", 0.0f).toDouble()

        return if (latitude != 0.0 && longitude != 0.0) {
            Location("").apply {
                this.latitude = latitude
                this.longitude = longitude
                this.altitude = elevation
            }
        } else {
            null
        }
    }

    @SuppressLint("CommitTransaction")
    private fun showFragment(bundle: Bundle) {
        val factory: CameraViewModelFactory = CameraViewModelFactory.getInstance(requireContext())
        val viewModel: CameraViewModel by viewModels { factory }
        val factoryRemote: CameraRemoteViewModelFactory = CameraRemoteViewModelFactory.getInstance(requireContext())
        val viewModelRemote: CameraRemoteViewModel by viewModels { factoryRemote }
        val saveFragment = SaveFragment()
        val detailRemoteFragment = DetailRemoteFragment()
        val detailFragment = DetailFragment()

        viewModel.cekId(resultId.toInt()).observe(viewLifecycleOwner) { cek: Entity? ->
            viewModelRemote.cekIdRemote(resultId.toInt()).observe(viewLifecycleOwner) { check: RemoteEntity? ->
                if (cek != null) {
                    showToast("Ini data lama")
                    detailFragment.arguments = bundle
                    requireView().findNavController().navigate(R.id.action_navigation_camera_to_navigation_detail, bundle)
                } else if (check != null) {
                    showToast("Ini data lama remote")
                    detailRemoteFragment.arguments = bundle
                    requireView().findNavController().navigate(R.id.action_navigation_camera_to_navigation_detail_remote, bundle)
                } else {
                    showToast("ini data baru")
                    saveFragment.arguments = bundle
                    requireView().findNavController().navigate(R.id.action_navigation_camera_to_navigation_save, bundle)
                }
            }
        }
    }

    private fun hideSystemUI(activity: Activity) {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "CameraFragment"
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}