package com.dicoding.geotaggingjbg.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.geotaggingjbg.R
import com.dicoding.geotaggingjbg.data.database.Entity
import com.dicoding.geotaggingjbg.databinding.FragmentHomeBinding
import com.dicoding.geotaggingjbg.ui.detail.DetailFragment
import com.dicoding.geotaggingjbg.ui.login.LoginActivity
import com.dicoding.geotaggingjbg.ui.utils.ResultState
import com.dicoding.geotaggingjbg.ui.utils.uriToFile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject

class HomeFragment : Fragment() {
    private val binding get() = _binding!!
    private var _binding: FragmentHomeBinding? = null
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory.getInstance(requireContext().applicationContext)
    }
    private var isLogout = false

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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSession().observe(requireActivity()) { user ->
            if (!user.isLogin) {
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
            }
        }
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvData.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvData.addItemDecoration(itemDecoration)

        viewModel.getPagingData().observe(viewLifecycleOwner) { pagingData ->
            viewModel.getData().observe(viewLifecycleOwner) { data ->
                Log.d("Get data", data.toString())
                if (data.isEmpty()) {
                    binding.tvNone.visibility = View.VISIBLE
                } else {
                    binding.tvNone.visibility = View.GONE
                }
            }
            // Membuat adapter PagingDataAdapter
            val adapter = HomeAdapter()

            // Memasukkan PagingData ke adapter
            viewLifecycleOwner.lifecycleScope.launch {
                adapter.submitData(pagingData)
            }

            // Menetapkan adapter ke RecyclerView
            binding.rvData.adapter = adapter

            // Menetapkan onClickListener untuk item di RecyclerView
            adapter.setOnItemClickCallback(object : HomeAdapter.OnItemClickCallback {
                override fun onItemClicked(data: Entity) {
                    val bundle = Bundle().apply {
                        putString("SCANNED_DATA", data.id.toString())
                    }
                    val detailFragment = DetailFragment()
                    detailFragment.arguments = bundle
                    requireView().findNavController()
                        .navigate(R.id.action_navigation_home_to_navigation_detail, bundle)
                }
            })

            // Menyembunyikan ProgressBar dan menampilkan RecyclerView
            binding.progressBar.visibility = View.GONE
            binding.rvData.visibility = View.VISIBLE
        }

        binding.btUpload.setOnClickListener {
            // Tampilkan dialog konfirmasi
            viewModel.getSession().observe(requireActivity()) { user ->
                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle("Konfirmasi Upload")
                alertDialogBuilder.setMessage("Apakah Anda yakin ingin mengupload data? Data yang saat ini akan dihapus dan diunggah ke server.")
                alertDialogBuilder.setPositiveButton("Ya") { _, _ ->
                    // Jika pengguna memilih "Ya", lanjutkan dengan proses upload
                    Log.d("HomeUpload", "ini upload")
                    viewModel.getData().observe(viewLifecycleOwner) { entities ->
                        var success = 0
                        if (entities.isNotEmpty()) {
                            showProgressBar()
                            Log.d("ini entities", "$entities")

                            entities.forEach{ entity ->
                                val size = entities.size
                                Log.d("TEST SIZE", "$size")
                                Log.d("ini entities dalam loop", "$entities")

                                val image = uriToFile(entity.image!!.toUri(), requireContext())
                                Log.d("ini Image", "$image")
                                Log.d("ini entity", "$entity")
                                viewModel.uploadImage(requireContext(), image, user.token)
                                val fileName = image.name

                                val data = JSONObject().apply {
                                    put("id_tanaman", entity.id)
                                    put("id_jenis", entity.jenTan)
                                    put("id_kegiatan", entity.kegiatan)
                                    put("id_lokasi", entity.lokasi)
                                    put("id_sk", entity.sk)
                                    put("id_status", entity.status)
                                    put("diameter", entity.diameter)
                                    put("tinggi", entity.tinggi)
                                    put("tanggal_tanam", entity.tanggal)
                                    put("date_modified", entity.tanggalModified)
                                    put("latitude", entity.latitude.toString())
                                    put("longitude", entity.longitude.toString())
                                    put("elevasi", entity.elevasi.toString())
                                    put("easting", entity.easting.toString())
                                    put("northing", entity.northing.toString())
                                    put("images", fileName)
                                    put("id_action", 1)
                                    put("uid", user.uid)
                                }
                                Log.d("ini data", "$data")
                                val description = RequestBody.create(
                                    "application/json".toMediaTypeOrNull(),
                                    data.toString()
                                )
                                Log.d("CEK DESC", "$description")
                                viewModel.uploadData(description, user.token)
                                    .observe(viewLifecycleOwner) { result ->
                                        when (result) {
                                            is ResultState.Success -> {
                                                showToast(result.data)
                                                hideProgressBar()
                                                success = 1
                                                Log.d("CEK SUCCESS", "SUCCESSS, $success")
                                            }
                                            is ResultState.Error -> {
                                                showToast(result.error)
                                                hideProgressBar()
                                                success = 0
                                                Log.d("CEK SUCCESS", "ERRORR, $success")
                                            }
                                            else -> {}

                                        }
                                        Log.d("CEK SUCCESS", "SUCCESS, $success")
                                        if (success == 1) {
                                            Log.d("UPLOAD DATA", "Success")
                                            viewModel.delete()
                                        }
                                    }
                            }

                        } else {
                            Log.d("HomeUpload", "Entity empty")
                        }
                    }
                }
                alertDialogBuilder.setNegativeButton("Tidak") { dialog, _ ->
                    // Jika pengguna memilih "Tidak", tutup dialog tanpa melakukan apa pun
                    dialog.dismiss()
                }
                alertDialogBuilder.show()
            }
        }
        binding.btLogout.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setTitle("Konfirmasi Logout")
            alertDialogBuilder.setMessage("Apakah anda yakin ingin logout?")
            alertDialogBuilder.setPositiveButton("Ya") { _, _ ->
                isLogout = true
                viewModel.logout()
            }
            alertDialogBuilder.setNegativeButton("Tidak") { dialog, _ ->
                isLogout = false
                dialog.dismiss()
            }
            alertDialogBuilder.show()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}