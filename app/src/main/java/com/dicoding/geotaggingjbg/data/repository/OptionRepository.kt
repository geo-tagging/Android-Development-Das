package com.dicoding.geotaggingjbg.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.dicoding.geotaggingjbg.data.database.AppDatabase
import com.dicoding.geotaggingjbg.data.database.Dao
import com.dicoding.geotaggingjbg.data.database.JenisEntity
import com.dicoding.geotaggingjbg.data.database.KegiatanEntity
import com.dicoding.geotaggingjbg.data.database.LokasiEntity
import com.dicoding.geotaggingjbg.data.database.PetakEntity
import com.dicoding.geotaggingjbg.data.database.RemoteEntity
import com.dicoding.geotaggingjbg.data.database.SkEntity
import com.dicoding.geotaggingjbg.data.database.SkKerjaEntity
import com.dicoding.geotaggingjbg.data.database.StatusAreaTanamEntity
import com.dicoding.geotaggingjbg.data.database.StatusEntity
import com.dicoding.geotaggingjbg.data.response.DataItem
import com.dicoding.geotaggingjbg.data.response.DownloadResponse
import com.dicoding.geotaggingjbg.data.response.OptionResponse
import com.dicoding.geotaggingjbg.data.response.UploadImageResponse
import com.dicoding.geotaggingjbg.data.response.UploadResponse
import com.dicoding.geotaggingjbg.data.response.toEntity
import com.dicoding.geotaggingjbg.data.retrofit.ApiService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OptionRepository(private val apiService: ApiService, context: Context) {
    private val dao: Dao

    init {
        val db = AppDatabase.getInstance(context)
        dao = db.dao()
    }

    fun getJenis(): List<JenisEntity> = dao.getAllJenis()
    fun getKegiatan(): List<KegiatanEntity> = dao.getAllKegiatan()
    fun getLokasi(): List<LokasiEntity> = dao.getAllLokasi()
    fun getStatus(): List<StatusEntity> = dao.getAllStatus()
    fun getSk(): List<SkEntity> = dao.getAllSk()
    fun getSkKerja(): List<SkKerjaEntity> = dao.getAllSkKerja()
    fun getStatusAreaTanam(): List<StatusAreaTanamEntity> = dao.getAllStatusAreaTanam()
    fun getPetak(): List<PetakEntity> = dao.getAllPetak()

    private fun deleteOptionFromDatabase() {
        dao.deleteJenis()
        dao.deleteKegiatan()
        dao.deleteLokasi()
        dao.deleteStatus()
        dao.deleteSk()
        dao.deleteSkKerja()
        dao.deleteStatusAreaTanam()
        dao.deletePetak()
    }

    fun optionToDatabase(token: String,): Call<OptionResponse> {
        Log.d("Option", "Mengambil data dari API")
        // Mendapatkan data dari API
        val call = apiService.getOption("Bearer $token")
        // Menyimpan data dari respons ke database lokal
        call.enqueue(object : Callback<OptionResponse> {
            override fun onResponse(
                call: Call<OptionResponse>,
                response: Response<OptionResponse>
            ) {
                if (response.isSuccessful) {
                    val optionResponse = response.body()
                    if (optionResponse != null) {
                        val jenisEntities = optionResponse.tbJenis.map { it.toEntity() }
                        Log.d("CEK ERROR","jenisEntities : $jenisEntities")
                        val kegiatanEntities = optionResponse.tbKegiatan.map { it.toEntity() }
                        Log.d("CEK ERROR","kegiatanEntities : $kegiatanEntities")
                        val lokasiEntities = optionResponse.tbLokasi.map { it.toEntity() }
                        Log.d("CEK ERROR","lokasiEntities : $lokasiEntities")
                        val statusEntities = optionResponse.tbStatus.map { it.toEntity() }
                        Log.d("CEK ERROR","statusEntities: $statusEntities")
                        val skEntities = optionResponse.tbSk.map { it.toEntity() }
                        Log.d("CEK ERROR","skEntities : $skEntities")
                        val skKerjaEntities = optionResponse.tbSkKerja.map { it.toEntity() }
                        Log.d("CEK ERROR","skKerjaEntities : $skKerjaEntities")
                        val statusAreaTanamEntities = optionResponse.tbStatusAreaTanam.map { it.toEntity() }
                        Log.d("CEK ERROR","statusAreaTanamEntities : $statusAreaTanamEntities")
                        val petakEntities = optionResponse.tbPetakUkur.map { it.toEntity() }
                        Log.d("CEK ERROR","petakEntities : $petakEntities")
                        // Menyimpan data ke database lokal menggunakan DAO
                        saveDataToLocalDatabase(
                            jenisEntities,
                            kegiatanEntities,
                            lokasiEntities,
                            statusEntities,
                            skEntities,
                            skKerjaEntities,
                            statusAreaTanamEntities,
                            petakEntities
                        )
                    } else {
                        Log.d("OptionRepository", "Response body is null")
                    }
                } else {
                    Log.d(
                        "OptionRepository",
                        "Failed to get data from API. Code: ${response.code()}"
                    )
                }
            }

            override fun onFailure(call: Call<OptionResponse>, t: Throwable) {
                Log.e("OptionRepository", "Failed to fetch data from API", t)
            }
        })

        return call
    }

    // Metode untuk menyimpan data ke database lokal menggunakan DAO
    @OptIn(DelicateCoroutinesApi::class)
    private fun saveDataToLocalDatabase(
        jenisList: List<JenisEntity>,
        kegiatanList: List<KegiatanEntity>,
        lokasiList: List<LokasiEntity>,
        statusList: List<StatusEntity>,
        skList: List<SkEntity>,
        skKerjaList: List<SkKerjaEntity>,
        statusAreaTanamList: List<StatusAreaTanamEntity>,
        petakList: List<PetakEntity>
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            deleteOptionFromDatabase()
            dao.insertAllJenis(jenisList)
            dao.insertAllKegiatan(kegiatanList)
            dao.insertAllLokasi(lokasiList)
            dao.insertAllStatus(statusList)
            dao.insertAllSk(skList)
            dao.insertAllSkKerja(skKerjaList)
            dao.insertAllStatusAreaTanam(statusAreaTanamList)
            dao.insertAllPetak(petakList)
        }
    }

    companion object {
        @Volatile
        private var instance: OptionRepository? = null
        fun getInstance(
            apiService: ApiService,
            context: Context
        ): OptionRepository = instance ?: synchronized(this) {
            instance ?: OptionRepository(apiService, context)
        }.also { instance = it }
    }
}