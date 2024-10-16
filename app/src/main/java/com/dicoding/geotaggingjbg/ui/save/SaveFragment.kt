package com.dicoding.geotaggingjbg.ui.save

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.dicoding.geotaggingjbg.BuildConfig
import com.dicoding.geotaggingjbg.R
import com.dicoding.geotaggingjbg.data.database.Entity
import com.dicoding.geotaggingjbg.databinding.FragmentSaveBinding
import com.dicoding.geotaggingjbg.ui.home.HomeViewModel
import com.dicoding.geotaggingjbg.ui.home.HomeViewModelFactory
import com.dicoding.geotaggingjbg.ui.utils.createCustomTempFile
import com.dicoding.geotaggingjbg.ui.utils.reduceFileImage
import com.dicoding.geotaggingjbg.ui.utils.uriToFile
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osgeo.proj4j.CRSFactory
import org.osgeo.proj4j.CoordinateTransformFactory
import org.osgeo.proj4j.ProjCoordinate
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class SaveFragment : Fragment() {

    private val binding get() = _binding!!
    private var _binding: FragmentSaveBinding? = null
    private val viewModel: SaveViewModel by viewModels {
        SaveViewModelFactory.getInstance(requireContext().applicationContext)
    }

    private var id: String = ""
    private var imageUri: Uri? = null

    private var latitude: String = "0.0"
    private var longitude: String = "0.0"
    private var elevation: String = "0.0"

    private var date: String = ""

    private var verif: Int = 1
    private val REQUEST_IMAGE_CAPTURE = 1

    private var selectedJenisId: Int = -1
    private var selectedLokasiId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Panggil fungsi untuk memuat data dari database
        viewModel.loadAllDataFromDatabase()

        if (arguments != null) {
            id = arguments?.getString("SCANNED_DATA", "ID").toString()
            Log.d("CEK FIRST CHAR OF ID SAVE1", id)

            if (id.isNotEmpty()) {
                Log.d("CEK FIRST CHAR OF ID SAVE2", "${id.first()}")

                if (checkId(id)) {
                    showDasField()

                    viewModel.skKerjaList.observe(viewLifecycleOwner) { skKerjaList ->
                        val adapterSkKerja = ArrayAdapter(
                            requireContext(),
                            R.layout.row_spinner,
                            skKerjaList.map { it.skKerja }
                        )
                        binding.spinSkKk.adapter = adapterSkKerja
                    }

                    // SK
                    viewModel.statusAreaTanamEntity.observe(viewLifecycleOwner) { statusAreaTanamList ->
                        val adapterStatusAreaTanam = ArrayAdapter(
                            requireContext(),
                            R.layout.row_spinner,
                            statusAreaTanamList.map { it.statusAreaTanam }
                        )
                        binding.spinStatusAreaTanam.adapter = adapterStatusAreaTanam
                    }

                    // Status
                    viewModel.petakList.observe(viewLifecycleOwner) { petakList ->
                        val adapterPetak = ArrayAdapter(
                            requireContext(),
                            R.layout.row_spinner,
                            petakList.map { it.petakUkur }
                        )
                        binding.spinPetak.adapter = adapterPetak
                    }
                }
            } else {
                Log.d("CEK FIRST CHAR OF ID SAVE3", "ID is empty.")
            }
        }

        viewModel.jenisList.observe(viewLifecycleOwner) { jenisList ->
            val jenisMap = jenisList.associate { it.id.toString() to it.nama }  // Mapping ID dan Nama

            val adapterJenis = ArrayAdapter(
                requireContext(),
                R.layout.row_spinner,
                jenisMap.values.toList()
            )
            (binding.autocompleteJentan as? AutoCompleteTextView)?.apply {
                setAdapter(adapterJenis)
                setOnItemClickListener { _, _, position, _ ->
                    val selectedName = adapterJenis.getItem(position) ?: ""
                    selectedJenisId = jenisMap.filterValues { it == selectedName }.keys.first().toInt()
                    Log.d("CEKLIST", "Selected jenis: $selectedName, ID: $selectedJenisId")
                }
            }
        }

        // Observe lokasiList dari ViewModel
        viewModel.lokasiList.observe(viewLifecycleOwner) { lokasiList ->
            val lokasiMap = lokasiList.associate { it.id.toString() to it.lokasi }  // Mapping ID dan Nama

            val adapterLokasi = ArrayAdapter(
                requireContext(),
                R.layout.row_spinner,
                lokasiMap.values.toList()  // Hanya ambil nama lokasi
            )
            (binding.autocompleteLokasi as? AutoCompleteTextView)?.apply {
                setAdapter(adapterLokasi)
                setOnItemClickListener { _, _, position, _ ->
                    val selectedName = adapterLokasi.getItem(position) ?: ""
                    selectedLokasiId = lokasiMap.filterValues { it == selectedName }.keys.first().toInt()
                    Log.d("CEKLIST", "Selected lokasi: $selectedName, ID: $selectedLokasiId")
                }
            }
        }

        // Kegiatan
        viewModel.kegiatanList.observe(viewLifecycleOwner) { kegiatanList ->
            val adapterKegiatan = ArrayAdapter(
                requireContext(),
                R.layout.row_spinner,
                kegiatanList.map { it.kegiatan }
            )
            binding.spinKegiatan.adapter = adapterKegiatan
        }

        // SK
        viewModel.skList.observe(viewLifecycleOwner) { skList ->
            val adapterSk = ArrayAdapter(
                requireContext(),
                R.layout.row_spinner,
                skList.map { it.sk }
            )
            binding.spinSk.adapter = adapterSk
        }

        // Status
        viewModel.statusList.observe(viewLifecycleOwner) { statusList ->
            val adapterStatus = ArrayAdapter(
                requireContext(),
                R.layout.row_spinner,
                statusList.map { it.status }
            )
            binding.spinStatus.adapter = adapterStatus
        }

        binding.btPilih.setOnClickListener {
            dispatchTakePictureIntent()
        }

        binding.iconDate.setOnClickListener {
            showDatePickerDialog()
        }

        if (arguments != null) {
            id = arguments?.getString("SCANNED_DATA", "ID").toString()
            binding.tvIdisi.text = id

            // Mendapatkan data longitude, latitude, dan elevasi dari Bundle
            latitude = arguments?.getDouble("latitude", 0.0).toString()
            longitude = arguments?.getDouble("longitude", 0.0).toString()
            elevation = arguments?.getDouble("elevation", 0.0).toString()
            val (easting, northing) = convertToUTM(latitude.toDouble(), longitude.toDouble())
            val elevationText = truncateToTwoDecimalPlaces(elevation.toDouble())

            // Menampilkan data longitude, latitude, dan elevasi di EditText yang sesuai
            binding.etLat.setText(easting.toString())
            binding.etLong.setText(northing.toString())
            binding.etElev.setText(elevationText.toString())

            date = getCurrentDateTime()
            binding.tvTanggalisi.text = getCurrentDateTimeForDisplay()
        }

        binding.btBatal.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.ivClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btSimpan.setOnClickListener {
            val longText = binding.etLong.text.toString()
            val langText = binding.etLat.text.toString()
            val elevText = binding.etElev.text.toString()
            val eastingText = binding.etLat.text.toString()
            val northingText = binding.etLong.text.toString()
            val tinggiET = binding.etTinggi.text.toString()
            val diaET = binding.etDia.text.toString()
            val tanggal = date

            var selectedPetak: String?
            var selectedSkKawasanKerja: String?
            var selectedStatusAreaTanam: String?

            if (checkId(id)) {
                selectedPetak = binding.spinPetak.selectedItem.toString()
                selectedSkKawasanKerja = binding.spinSkKk.selectedItem.toString()
                selectedStatusAreaTanam = binding.spinStatusAreaTanam.selectedItem.toString()
            } else {
                selectedPetak = null
                Log.d("CEK NULL DAS FIELD1", "$selectedPetak")
                selectedSkKawasanKerja = null
                Log.d("CEK NULL DAS FIELD2", "$selectedSkKawasanKerja")
                selectedStatusAreaTanam = null
                Log.d("CEK NULL DAS FIELD3", "$selectedStatusAreaTanam")
            }

            Log.d("isi imageuri", imageUri.toString())

            if (longText.isNotEmpty() && langText.isNotEmpty() && elevText.isNotEmpty() &&
                tinggiET.isNotEmpty() && diaET.isNotEmpty() && imageUri != null
                //TODO("BUAT KONDISI AGAR USER TIDAK BISA MENYIMPAN DATA BISA BELUM LENGKAP")
//                if (longText.isNotEmpty() && langText.isNotEmpty() && elevText.isNotEmpty() &&
//                    (selectedKegiatan.isNotEmpty() && selectedKegiatan != spinnerIdKegiatan[0]) && (selectedSk.isNotEmpty() && selectedSk != spinnerIdSk[0]) && (selectedStatus.isNotEmpty() && selectedStatus != spinnerIdStatus[0]) &&
//                    tinggiET.isNotEmpty() && diaET.isNotEmpty() && imageUri != null
            ) {
                val data = Entity(
                    image = imageUri.toString(),
                    id = id.toInt(),

                    jenTan = selectedJenisId,
                    lokasi = selectedLokasiId,
                    kegiatan = binding.spinKegiatan.selectedItemId.toInt() + 1,
                    sk = binding.spinSk.selectedItemId.toInt() + 1,
                    status = binding.spinStatus.selectedItemId.toInt() + 1,
                    skKerja = if (checkId(id)) binding.spinSkKk.selectedItemId.toInt() + 1 else null,
                    statusAreaTanam = if (checkId(id)) binding.spinStatusAreaTanam.selectedItemId.toInt() + 1 else null,
                    petak = if (checkId(id)) binding.spinPetak.selectedItemId.toInt() + 1 else null,

                    tanggalModified = getCurrentDateTime(),
                    tanggal = tanggal,
                    tinggi = tinggiET.toDouble(),
                    diameter = diaET.toDouble(),

                    latitude = latitude.toDouble(),
                    longitude = longitude.toDouble(),
                    elevasi = elevText.toDouble(),
                    easting = eastingText.toDouble(),
                    northing = northingText.toDouble(),

                    verif = verif
                )
                viewModel.saveImageLocal(data)
                showToast("Data telah berhasil disimpan!")
                it.findNavController().navigate(R.id.action_navigation_save_to_navigation_home)
            } else {
                showToast("Harap isi semua data")
            }
        }
    }

    private fun showDasField() {
        binding.tvPetak.visibility = View.VISIBLE
        binding.spinPetak.visibility = View.VISIBLE
        binding.tvSkKk.visibility = View.VISIBLE
        binding.spinSkKk.visibility = View.VISIBLE
        binding.tvStatusAreaTanam.visibility = View.VISIBLE
        binding.spinStatusAreaTanam.visibility = View.VISIBLE
    }

    private fun checkId(id: String): Boolean {
        val isDas: Boolean = id.first() == '1'
        return isDas
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_IMAGE_CAPTURE
            )
        } else {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                    val photoFile: File? = try {
                        createCustomTempFile(requireContext())
                    } catch (ex: Exception) {
                        null
                    }
                    photoFile?.also {
                        imageUri = FileProvider.getUriForFile(
                            requireContext(),
                            "${BuildConfig.APPLICATION_ID}.fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == android.app.Activity.RESULT_OK) {
            // Mendapatkan File dari URI
            val imageFile = uriToFile(imageUri!!, requireContext())
            // Mengurangi ukuran gambar
            val reducedFile = imageFile.reduceFileImage()
//            val resolutionFile = reduceFileSize(reducedFile, 600, 800)
            // Menetapkan URI dari file yang telah dikurangi ukurannya ke ImageView
            reduceImageAsync(reducedFile) { compressedFile ->
                val compressedUri = Uri.fromFile(compressedFile)
                imageUri = compressedUri
                binding.cvImagePreview.setImageURI(compressedUri)
            }
        }
    }

    private fun reduceImageAsync(imageFile: File, callback: (File) -> Unit) {
        // Menjalankan kompresi gambar secara asinkron di lifecycleScope
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Kompresi gambar
                val compressedFile = Compressor.compress(requireContext(), imageFile)
                // Panggil callback dengan file yang telah dikompresi
                callback(compressedFile)
            } catch (e: Exception) {
                // Tangani kesalahan jika terjadi kesalahan dalam kompresi
                e.printStackTrace()
            }
        }
    }

    private fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("GMT+8:00")
        return dateFormat.format(calendar.time)
    }

    private fun getCurrentDateTimeForDisplay(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("GMT+8:00")
        return dateFormat.format(calendar.time)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val dateFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                dateFormat.timeZone = TimeZone.getTimeZone("GMT+8:00")
                val formattedDate = dateFormat.format(selectedDate.time)
                val dateFormatForTextDisplay = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                dateFormatForTextDisplay.timeZone = TimeZone.getTimeZone("GMT+8:00")
                val dateForTextDisplay = dateFormatForTextDisplay.format(selectedDate.time)
                date = formattedDate
                binding.tvTanggalisi.text = dateForTextDisplay
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun convertToUTM(latitude: Double, longitude: Double): Pair<Double, Double> {
        // Membuat CoordinateReferenceSystem untuk WGS84 (latitude dan longitude)
        val crsFactory = CRSFactory()
        val utmCrs = crsFactory.createFromName("EPSG:32750") // UTM zone 50M
        // Koordinat untuk WGS84
        val wgs84Coordinate = ProjCoordinate(longitude, latitude)
        // Membuat objek CoordinateReferenceSystem untuk UTM
        val wgs84Crs =
            crsFactory.createFromParameters("WGS84", "+proj=longlat +datum=WGS84 +no_defs")
        // Membuat CoordinateTransformFactory
        val ctFactory = CoordinateTransformFactory()
        val transform = ctFactory.createTransform(wgs84Crs, utmCrs)
        // Melakukan transformasi dari WGS84 ke UTM
        val utmCoordinate = ProjCoordinate()
        transform.transform(wgs84Coordinate, utmCoordinate)
        // Membulatkan koordinat UTM menjadi dua angka desimal
        Log.d("tag utm", utmCoordinate.x.toString() + "," + utmCoordinate.y.toString())
        val easting = truncateToTwoDecimalPlaces(utmCoordinate.x)
        val northing = truncateToTwoDecimalPlaces(utmCoordinate.y)
        // Mengembalikan koordinat UTM
        return Pair(easting, northing)
    }

    private fun truncateToTwoDecimalPlaces(value: Double): Double {
        val stringValue = value.toString()
        val dotIndex = stringValue.indexOf(".")
        return if (dotIndex == -1 || dotIndex + 3 >= stringValue.length) {
            value
        } else {
            stringValue.substring(0, dotIndex + 3).toDouble()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        var EXTRA_FILE = "extra_file"
    }
}