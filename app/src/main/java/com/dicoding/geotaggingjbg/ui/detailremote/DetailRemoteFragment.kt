package com.dicoding.geotaggingjbg.ui.detailremote

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
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import java.io.FileOutputStream
import java.io.IOException
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

    private lateinit var binding: FragmentDetailRemoteBinding
    private lateinit var viewModel: DetailRemoteViewModel

    private lateinit var spinnerItemJenis: Array<String>
    private lateinit var spinnerItemLokasi: Array<String>
    private lateinit var spinnerItemKegiatan: Array<String>
    private lateinit var spinnerItemSk: Array<String>
    private lateinit var spinnerItemStatus: Array<String>
    private lateinit var spinnerItemPetak: Array<String>
    private lateinit var spinnerItemSkKawasanKerja: Array<String>
    private lateinit var spinnerItemStatusAreaTanam: Array<String>

    private lateinit var spinnerIdPetak: List<String>
    private lateinit var spinnerIdSkKawasanKerja: List<String>
    private lateinit var spinnerIdStatusAreaTanam: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailRemoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getString("SCANNED_DATA").toString().toInt()
        val factory = DetailRemoteViewModelFactory.createFactory(requireActivity(), id)

        viewModel = ViewModelProvider(this, factory)[DetailRemoteViewModel::class.java]
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
                        diameter = etDia.text.toString().toDouble()

                        //TODO: tambahkan petak, skKawasanKerja, statusAreaTanam dengan kondisi cek id

                    )
                    viewModel.saveLocal(data)
                    showToast("Data telah berhasil disimpan!")
                }
                it.findNavController()
                    .navigate(R.id.action_navigation_detail_remote_to_navigation_home)
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
        entity.apply {
            binding.apply {
                imageUri = entity.images?.toUri()
                val id = entity.id_tanaman
                date = entity.tanggal_tanam
                val jenTanId = entity.id_jenis - 1
                val lokasiId = entity.id_lokasi - 1
                val kegiatanId = entity.id_kegiatan - 1
                val skId = entity.id_sk - 1
                val statusId = entity.id_status - 1
                val tinggi = entity.tinggi
                val diameter = entity.diameter

                val idStr = id.toString()

                spinnerItemJenis = resources.getStringArray(R.array.array_jentan)
                val spinnerIdJenis = spinnerItemJenis.map { it.split(",")[1] }.drop(1)
                val adapterJenis = ArrayAdapter(
                    requireContext(),
                    R.layout.row_spinner,
                    spinnerIdJenis
                )
                adapterJenis.setDropDownViewResource(R.layout.row_spinners_dropdown)
                binding.spinJentan.adapter = adapterJenis

                spinnerItemLokasi = resources.getStringArray(R.array.array_lokasi)
                val spinnerIdLokasi = spinnerItemLokasi.map { it.split(",")[1] }.drop(1)
                val adapterLokasi = ArrayAdapter(
                    requireContext(),
                    R.layout.row_spinner,
                    spinnerIdLokasi
                )
                adapterLokasi.setDropDownViewResource(R.layout.row_spinners_dropdown)
                binding.spinLokasi.adapter = adapterLokasi

                spinnerItemKegiatan = resources.getStringArray(R.array.array_kegiatan)
                val spinnerIdKegiatan = spinnerItemKegiatan.map { it.split(",")[1] }.drop(1)
                val adapterKegiatan = ArrayAdapter(
                    requireContext(),
                    R.layout.row_spinner,
                    spinnerIdKegiatan
                )
                adapterKegiatan.setDropDownViewResource(R.layout.row_spinners_dropdown)
                binding.spinKegiatan.adapter = adapterKegiatan

                spinnerItemSk = resources.getStringArray(R.array.array_sk)
                val spinnerIdSk = spinnerItemSk.map { it.split(",")[1] }.drop(1)
                val adapterSk = ArrayAdapter(
                    requireContext(),
                    R.layout.row_spinner,
                    spinnerIdSk
                )
                adapterSk.setDropDownViewResource(R.layout.row_spinners_dropdown)
                binding.spinSk.adapter = adapterSk

                spinnerItemStatus = resources.getStringArray(R.array.array_status)
                val spinnerIdStatus = spinnerItemStatus.map { it.split(",")[1] }.drop(1)
                val adapterStatus = ArrayAdapter(
                    requireContext(),
                    R.layout.row_spinner,
                    spinnerIdStatus
                )
                adapterStatus.setDropDownViewResource(R.layout.row_spinners_dropdown)
                binding.spinStatus.adapter = adapterStatus

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

                for ((index, item) in spinnerItemJenis.withIndex()) {
                    if (item.split(",")[0].toInt() == jenTanId) {
                        spinJentan.setSelection(index)
                        break
                    }
                }

                for ((index, item) in spinnerItemLokasi.withIndex()) {
                    if (item.split(",")[0].toInt() == lokasiId) {
                        spinLokasi.setSelection(index)
                        break
                    }
                }

                for ((index, item) in spinnerItemKegiatan.withIndex()) {
                    if (item.split(",")[0].toInt() == kegiatanId) {
                        spinKegiatan.setSelection(index)
                        break
                    }
                }

                for ((index, item) in spinnerItemSk.withIndex()) {
                    if (item.split(",")[0].toInt() == skId) {
                        spinSk.setSelection(index)
                        break
                    }
                }

                for ((index, item) in spinnerItemStatus.withIndex()) {
                    if (item.split(",")[0].toInt() == statusId) {
                        spinStatus.setSelection(index)
                        break
                    }
                }

                Log.d("CEK FIRST CHAR OF ID DETAILREMOTE1", idStr)

                if (idStr.isNotEmpty()) {
                    Log.d("CEK FIRST CHAR OF ID DETAILREMOTE2", "${idStr.first()}")
                    if (checkId(idStr)) {
                        showDasField()

                        spinnerItemPetak = resources.getStringArray(R.array.array_petak)
                        spinnerIdPetak = spinnerItemPetak.map { it.split(",")[1] }
                        val adapterPetak = ArrayAdapter(
                            requireContext(),
                            R.layout.row_spinner,
                            spinnerIdPetak
                        )
                        adapterPetak.setDropDownViewResource(R.layout.row_spinners_dropdown)
                        spinPetak.adapter = adapterPetak

                        spinnerItemSkKawasanKerja = resources.getStringArray(R.array.array_sk_kk)
                        spinnerIdSkKawasanKerja = spinnerItemSkKawasanKerja.map { it.split(",")[1] }
                        val adapterSkKawasanKerja = ArrayAdapter(
                            requireContext(),
                            R.layout.row_spinner,
                            spinnerIdSkKawasanKerja
                        )
                        adapterSkKawasanKerja.setDropDownViewResource(R.layout.row_spinners_dropdown)
                        spinSkKk.adapter = adapterSkKawasanKerja

                        spinnerItemStatusAreaTanam = resources.getStringArray(R.array.array_status_area_tanam)
                        spinnerIdStatusAreaTanam = spinnerItemStatusAreaTanam.map { it.split(",")[1] }
                        val adapterStatusAreaTanam = ArrayAdapter(
                            requireContext(),
                            R.layout.row_spinner,
                            spinnerIdStatusAreaTanam
                        )
                        adapterStatusAreaTanam.setDropDownViewResource(R.layout.row_spinners_dropdown)
                        spinStatusAreaTanam.adapter = adapterStatusAreaTanam

                        //TODO: GET DATA FROM REMOTE DATABASE AND SET IT INTO THE VIEW
                    }
                } else {
                    Log.d("CEK FIRST CHAR OF ID DETAILREMOTE3", "ID is empty.")
                }

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

    private fun checkId(id: String) : Boolean {
        val isDas : Boolean = id.first() == '1'
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

    private fun reduceFileSize(file: File, maxWidth: Int, maxHeight: Int): File? {
        try {
            // Decode the image file to get its dimensions
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(file.absolutePath, options)

            // Calculate the scaling factor
            val scaleFactor = Math.min(
                options.outWidth / maxWidth,
                options.outHeight / maxHeight
            )

            // Decode the image file into a smaller bitmap
            options.inJustDecodeBounds = false
            options.inSampleSize = scaleFactor
            val resizedBitmap = BitmapFactory.decodeFile(file.absolutePath, options)

            // Create a new file to save the resized bitmap
            val resizedFile = createCustomTempFile(requireContext())

            // Save the resized bitmap to the new file
            FileOutputStream(resizedFile).use { out ->
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }

            return resizedFile
        } catch (e: IOException) {
            e.printStackTrace()
            return null
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

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val extraData = "Data"
    }
}