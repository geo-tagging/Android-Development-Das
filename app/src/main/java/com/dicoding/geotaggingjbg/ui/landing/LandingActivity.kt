package com.dicoding.geotaggingjbg.ui.landing

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.dicoding.geotaggingjbg.ui.MainActivity
import com.dicoding.geotaggingjbg.R
import com.dicoding.geotaggingjbg.databinding.ActivityLandingBinding
import com.dicoding.geotaggingjbg.ui.helper.Result
import com.dicoding.geotaggingjbg.ui.home.HomeFragment
import com.dicoding.geotaggingjbg.ui.login.LoginActivity

class LandingActivity : AppCompatActivity() {
    private val viewModel by viewModels<LandingViewModel> {
        LandingViewModelFactory.getInstance(this, 0)
    }
    private lateinit var binding: ActivityLandingBinding
    private lateinit var spinnerItemLokasi: Array<String>
    private lateinit var lokasiNames: Array<String>
    private lateinit var stringLokasi: String
    private var selectedValue: String? = null
    private var dialogShown = false
    private var lokasiMap = mutableMapOf<Int, String>()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG)
                    .show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        viewModel.getSession().observe(this) { user ->
//            if (user.isLogin == false) {
//                startActivity(Intent(this, LoginActivity::class.java))
//                finish()
//            }
            val currentTime = System.currentTimeMillis()

            // Jika user tidak login atau token sudah kedaluwarsa, minta user login ulang
            if (!user.isLogin || user.expireTime < currentTime) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                // Token masih valid, lanjutkan proses
                Log.d("MainActivity", "Token masih valid: ${user.token}")
            }
        }

        // Observe lokasiList dari ViewModel
        viewModel.lokasiList.observe(this) { lokasiList ->
            //KODE BARU
            if (lokasiList.isNullOrEmpty()) {
                // Database kosong, ambil data dari strings.xml
                spinnerItemLokasi = resources.getStringArray(R.array.array_lokasi)

                // Pemetaan lokasi dengan ID dan nama
                val lokasiMap = spinnerItemLokasi.map {
                    val parts = it.split(",")
                    parts[0] to parts[1]  // (ID, Nama)
                }.toMap()

                // Ambil hanya nama lokasi untuk ditampilkan di AutoCompleteTextView
                val lokasiNames = lokasiMap.values.toList()

                // Adapter untuk AutoCompleteTextView
                val adapterLokasi = ArrayAdapter(this, R.layout.row_spinner, lokasiNames)
                binding.autocompleteLokasi.setAdapter(adapterLokasi)

                // Listener untuk item AutoCompleteTextView
                binding.autocompleteLokasi.setOnItemClickListener { parent, _, position, _ ->
                    selectedValue = parent.getItemAtPosition(position) as String
                    // Cari ID berdasarkan nama yang dipilih
                    val selectedId = lokasiMap.filterValues { it == selectedValue }.keys.first()

                    Log.d("CEKLIST", "Selected lokasi: $selectedValue, ID: $selectedId")

//                    if (selectedValue == lokasiNames[0]) {
//                        binding.btLewat.isEnabled = false
//                    } else {
//                        binding.btLewat.isEnabled = true
//                        showDownloadDialog()
//                    }
                    showDownloadDialog()
                }
            } else {
                // Database ada isinya, ambil dari database
                val lokasiNames = lokasiList.map { it.lokasi }.toTypedArray()

                // Pemetaan lokasi dengan ID dan nama
                lokasiMap = lokasiList.associate { it.id to it.lokasi }.toMutableMap()

                // Adapter untuk AutoCompleteTextView
                val lokasiAdapter = ArrayAdapter(this, R.layout.row_spinner, lokasiNames)
                binding.autocompleteLokasi.setAdapter(lokasiAdapter)

                // Listener untuk item AutoCompleteTextView
                binding.autocompleteLokasi.setOnItemClickListener { parent, _, position, _ ->
                    selectedValue = parent.getItemAtPosition(position) as String
                    // Cari ID berdasarkan nama yang dipilih
                    val selectedId = lokasiMap.filterValues { it == selectedValue }.keys.first()

                    Log.d("CEKLIST", "Selected lokasi: $selectedValue, ID: $selectedId")

//                    if (selectedValue == lokasiNames[0]) {
//                        binding.btLewat.isEnabled = false
//                    } else {
//                        binding.btLewat.isEnabled = true
//                        showDownloadDialog()
//                    }
                    showDownloadDialog()
                }
            }
        }

        // Load data lokasi dari database
        viewModel.loadLokasiFromDatabase()
        binding.btLewat.isEnabled = false
//        binding.btLewat.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun showDownloadDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        viewModel.getSession().observe(this) { user ->
            builder.setTitle("Apakah Anda mau mengunduh?")
                .setPositiveButton("Ya") { dialog, which ->
                    // Disini Anda dapat menambahkan logika untuk menyimpan nilai yang dipilih
                    stringLokasi = selectedValue.toString()
                    Log.d("Unggah selectedValue", "$selectedValue")
                    Log.d("Unggah stringLokasi", stringLokasi)
                    Log.d("Unggah", "Ini Landing Activity")
                    viewModel.deleteData()
                    viewModel.fetchDataAndSaveToDatabase(user.token, stringLokasi)
                    viewModel.optionToDatabase(user.token)
                    dialogShown = false // Reset dialogShown setelah dialog ditutup
                    binding.btLewat.setOnClickListener {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
                .setNegativeButton("Tidak") { dialog, which ->
                    dialog.dismiss()
                    dialogShown = false // Reset dialogShown setelah dialog ditutup
                }
                .setOnDismissListener {
                    dialogShown = false // Reset dialogShown jika dialog ditutup tanpa dipilih
                }
                .show()
            dialogShown = true // Set dialogShown menjadi true saat dialog ditampilkan
            viewModel.result.observe(this) { result ->
                when (result) {
                    is Result.Success -> {
                        showToast("Data berhasil diambil!")
                        binding.btLewat.isEnabled = true
                        binding.btLewat.setOnClickListener {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }

                    is Result.Error -> {
                        showToast("Gagal mengambil data. Silakan coba lagi.")
                        binding.btLewat.isEnabled = true
                        binding.btLewat.setOnClickListener {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}