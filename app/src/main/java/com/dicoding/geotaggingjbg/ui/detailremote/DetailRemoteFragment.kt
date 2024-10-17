package com.dicoding.geotaggingjbg.ui.detailremote

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.dicoding.geotaggingjbg.BuildConfig
import com.dicoding.geotaggingjbg.R
import com.dicoding.geotaggingjbg.data.database.Entity
import com.dicoding.geotaggingjbg.data.database.RemoteEntity
import com.dicoding.geotaggingjbg.databinding.FragmentDetailRemoteBinding
import com.dicoding.geotaggingjbg.ui.utils.createCustomTempFile
import com.dicoding.geotaggingjbg.ui.utils.reduceFileImage
import com.dicoding.geotaggingjbg.ui.utils.uriToFile
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DetailRemoteFragment : Fragment() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private var imageUri: Uri? = null

    private var date: String? = ""

    private val binding get() = _binding!!
    private var _binding: FragmentDetailRemoteBinding? = null
    private val viewModel: DetailRemoteViewModel by viewModels {
        DetailRemoteViewModelFactory.createFactory(requireActivity(), id)
    }

    private var id: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailRemoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        id = arguments?.getInt("SCANNED_DATA") ?: 0
        Log.d("CEK ID DETAIL REMOTE", "$id")
        viewModel.loadAllDataFromDatabase()
        viewModel.getRemoteData.observe(viewLifecycleOwner) { remoteEntity ->
            showDetail(remoteEntity)
        }

        binding.ivClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btSimpan.setOnClickListener {
            if (imageUri != null) {
                binding.apply {
                    val data = Entity(
                        id = id,
                        image = imageUri.toString(),
                        tanggal = date,
                        tanggalModified = getCurrentDateTime(),
                        easting = etLat.text.toString().toDouble(),
                        northing = etLong.text.toString().toDouble(),
                        elevasi = etElev.text.toString().toDouble(),
                        jenTan = spinJentan.selectedItemId.toInt() + 1,
                        lokasi = spinLokasi.selectedItemId.toInt() + 1,
                        kegiatan = spinKegiatan.selectedItemId.toInt() + 1,
                        sk = spinSk.selectedItemId.toInt() + 1,
                        status = spinStatus.selectedItemId.toInt() + 1,
                        tinggi = etTinggi.text.toString().toDouble(),
                        diameter = etDia.text.toString().toDouble(),
                        skKerja = if (checkId(id.toString())) spinSkKk.selectedItemId.toInt() + 1 else null,
                        statusAreaTanam = if (checkId(id.toString())) spinStatusAreaTanam.selectedItemId.toInt() + 1 else null,
                        petak = spinPetak.selectedItemId.toInt() + 1
                    )
                    viewModel.saveLocal(data)
                    showToast("Data telah berhasil disimpan!")
                }
                it.findNavController().navigate(R.id.action_navigation_detail_remote_to_navigation_home)
            } else {
                showToast("Harap ambil gambar terlebih dahulu!")
            }
        }

        binding.btBatal.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btPilih.setOnClickListener {
            dispatchTakePictureIntent()
        }

        binding.iconDate.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDetail(entity: RemoteEntity) {
        Log.d("CEK DATA ENTITY ID DETAIL REMOTE", entity.toString())
        entity.apply {
            binding.apply {
                imageUri = entity.images?.toUri()
                val id = entity.id_tanaman
                date = entity.tanggal_tanam
                val jenTanId = entity.id_jenis
                val lokasiId = entity.id_lokasi
                val kegiatanId = entity.id_kegiatan
                val skId = entity.id_sk
                val statusId = entity.id_status
                val skKerjaId = entity.id_skKerja
                val statusAreaTanamId = entity.id_statusAreaTanam
                val petakId = entity.id_petak
                val tinggi = entity.tinggi
                val diameter = entity.diameter

                val idStr = id.toString()

                // Observasi data dari ViewModel dan update adapter spinner
                viewModel.jenisList.observe(viewLifecycleOwner) { jenisList ->
                    val adapterJenis = ArrayAdapter(
                        requireContext(),
                        R.layout.row_spinner,
                        jenisList.map { it.nama }
                    )
                    binding.spinJentan.adapter = adapterJenis

                    // Cari posisi dari jenTanId dan set spinner
                    val indexJenis = jenisList.indexOfFirst { it.id == jenTanId }
                    if (indexJenis >= 0) {
                        binding.spinJentan.setSelection(indexJenis)
                    }
                }

                viewModel.lokasiList.observe(viewLifecycleOwner) { lokasiList ->
                    val adapterLokasi = ArrayAdapter(
                        requireContext(),
                        R.layout.row_spinner,
                        lokasiList.map { it.lokasi }
                    )
                    binding.spinLokasi.adapter = adapterLokasi

                    val indexLokasi = lokasiList.indexOfFirst { it.id == lokasiId }
                    if (indexLokasi >= 0) {
                        binding.spinLokasi.setSelection(indexLokasi)
                    }
                }

                viewModel.kegiatanList.observe(viewLifecycleOwner) { kegiatanList ->
                    val adapterKegiatan = ArrayAdapter(
                        requireContext(),
                        R.layout.row_spinner,
                        kegiatanList.map { it.kegiatan }
                    )
                    binding.spinKegiatan.adapter = adapterKegiatan

                    val indexKegiatan = kegiatanList.indexOfFirst { it.id == kegiatanId }
                    if (indexKegiatan >= 0) {
                        binding.spinKegiatan.setSelection(indexKegiatan)
                    }
                }

                viewModel.skList.observe(viewLifecycleOwner) { skList ->
                    val adapterSk = ArrayAdapter(
                        requireContext(),
                        R.layout.row_spinner,
                        skList.map { it.sk }
                    )
                    binding.spinSk.adapter = adapterSk

                    val indexSk = skList.indexOfFirst { it.id == skId }
                    if (indexSk >= 0) {
                        binding.spinSk.setSelection(indexSk)
                    }
                }

                viewModel.statusList.observe(viewLifecycleOwner) { statusList ->
                    val adapterStatus = ArrayAdapter(
                        requireContext(),
                        R.layout.row_spinner,
                        statusList.map { it.status }
                    )
                    binding.spinStatus.adapter = adapterStatus

                    val indexStatus = statusList.indexOfFirst { it.id == statusId }
                    if (indexStatus >= 0) {
                        binding.spinStatus.setSelection(indexStatus)
                    }
                }

                viewModel.petakList.observe(viewLifecycleOwner) { petakList ->
                    val adapterPetak = ArrayAdapter(
                        requireContext(),
                        R.layout.row_spinner,
                        petakList.map { it.petakUkur }
                    )
                    binding.spinPetak.adapter = adapterPetak
                    val indexPetak = petakList.indexOfFirst { it.id == petakId }
                    if (indexPetak >= 0) {
                        binding.spinPetak.setSelection(indexPetak)
                    }
                }

                cvImagePreview.setImageURI(imageUri)
                tvIdisi.text = id.toString()
                tvTanggalisi.text = date?.let { parseDate(it) }
                etTinggi.setText(tinggi.toString())
                etDia.setText(diameter.toString())

                val latEdit = entity.easting
                etLat.setText(latEdit.toString())
                val longEdit = entity.northing
                etLong.setText(longEdit.toString())
                val elevEdit = entity.elevasi
                etElev.setText(elevEdit.toString())

                Log.d("CEK FIRST CHAR OF ID DETAILREMOTE1", idStr)

                if (idStr.isNotEmpty()) {
                    Log.d("CEK FIRST CHAR OF ID DETAILREMOTE2", "${idStr.first()}")
                    if (checkId(idStr)) {
                        showDasField()

                        viewModel.skKerjaList.observe(viewLifecycleOwner) { skKerjaList ->
                            val adapterSkKerja = ArrayAdapter(
                                requireContext(),
                                R.layout.row_spinner,
                                skKerjaList.map { it.skKerja }
                            )
                            binding.spinSkKk.adapter = adapterSkKerja
                            val indexSkKerja = skKerjaList.indexOfFirst { it.id == skKerjaId }
                            if (indexSkKerja >= 0) {
                                binding.spinSkKk.setSelection(indexSkKerja)
                            }
                        }

                        viewModel.statusAreaTanamEntity.observe(viewLifecycleOwner) { statusAreaTanamList ->
                            val adapterStatusAreaTanam = ArrayAdapter(
                                requireContext(),
                                R.layout.row_spinner,
                                statusAreaTanamList.map { it.statusAreaTanam }
                            )
                            binding.spinStatusAreaTanam.adapter = adapterStatusAreaTanam
                            val indexStatusAreaTanam = statusAreaTanamList.indexOfFirst { it.id == statusAreaTanamId }
                            if (indexStatusAreaTanam >= 0) {
                                binding.spinStatusAreaTanam.setSelection(indexStatusAreaTanam)
                            }
                        }
                    }
                } else {
                    Log.d("CEK FIRST CHAR OF ID DETAILREMOTE3", "ID is empty.")
                }
            }
        }
    }

    private fun showDasField() {
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
                takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
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

    private fun parseDate(date: String): String {
        var outputDate: String
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("GMT+8:00")
        try {
            val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getTimeZone("GMT+8:00")
            outputDate = outputFormat.format((inputFormat.parse(date) ?: Date()).time)
        } catch (e: ParseException) {
            e.printStackTrace()
            outputDate = date
        }
        return outputDate
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
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

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val extraData = "Data"
    }
}