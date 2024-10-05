package com.dicoding.geotaggingjbg.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.geotaggingjbg.data.database.AppDatabase
import com.dicoding.geotaggingjbg.data.database.Dao
import com.dicoding.geotaggingjbg.data.database.RemoteEntity
import com.dicoding.geotaggingjbg.data.response.DataItem
import com.dicoding.geotaggingjbg.data.response.DownloadResponse
import com.dicoding.geotaggingjbg.data.response.UploadImageResponse
import com.dicoding.geotaggingjbg.data.response.UploadResponse
import com.dicoding.geotaggingjbg.data.retrofit.ApiService
import com.dicoding.geotaggingjbg.ui.utils.ResultState
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class RemoteRepository(private val apiService: ApiService, context: Context) {
    private var dao: Dao

    init {
        val db = AppDatabase.getInstance(context)
        dao = db.dao()
    }

    fun fetchDataAndSaveToDatabase(token: String, keyword: String): Call<DownloadResponse> {
        Log.d("Unggah", "Ini Remote Repository $keyword")
        // Mendapatkan data dari API
        val call = apiService.getDatabyLocation("Bearer $token", keyword)
        // Menyimpan data dari respons ke database lokal
        call.enqueue(object : Callback<DownloadResponse> {
            override fun onResponse(
                call: Call<DownloadResponse>,
                response: Response<DownloadResponse>
            ) {
                if (response.isSuccessful) {
                    val downloadResponse = response.body()
                    if (downloadResponse != null) {
                        // Menyimpan data ke database lokal menggunakan DAO
                        saveDataToLocalDatabase(downloadResponse.data)
                    } else {
                        Log.d("RemoteRepository", "Response body is null")
                    }
                } else {
                    Log.d(
                        "RemoteRepository",
                        "Failed to get data from API. Code: ${response.code()}"
                    )
                }
            }

            override fun onFailure(call: Call<DownloadResponse>, t: Throwable) {
                Log.e("RemoteRepository", "Failed to fetch data from API", t)
            }
        })

        return call
    }

    // Metode untuk menyimpan data ke database lokal menggunakan DAO
    @OptIn(DelicateCoroutinesApi::class)
    private fun saveDataToLocalDatabase(data: List<DataItem>) {
        GlobalScope.launch(Dispatchers.IO) {
            dao.deleteData()
            for (entity in data) {
                dao.insertRemote(mapToRemoteEntity(entity))
            }
        }
    }
    fun deleteDataFromLocalDatabase() {
        dao.deleteData()
    }

    fun getById(id:Int): LiveData<RemoteEntity> {
        return dao.getDataRemotebyId(id)
    }

    private fun mapToRemoteEntity(dataItem: DataItem): RemoteEntity {
        return RemoteEntity(
            id_tanaman = dataItem.idTanaman,
            id_jenis = dataItem.idJenis,
            id_kegiatan = dataItem.idKegiatan,
            id_lokasi = dataItem.idLokasi,
            id_sk = dataItem.idSk,
            id_status = dataItem.idStatus,
            tanggal_tanam = dataItem.tanggalTanam,
            tinggi = dataItem.tinggi.toDouble(),
            diameter = dataItem.diameter.toDouble(),
            longitude = dataItem.longitude.toDouble(),
            latitude = dataItem.latitude.toDouble(),
            elevasi = dataItem.elevasi.toDouble(),
            easting = dataItem.easting.toDouble(),
            northing = dataItem.northing.toDouble(),
            images = dataItem.images,
            id_action = dataItem.idAction
        )
    }

    suspend fun uploadData(
        description: RequestBody, token: String
    ): UploadResponse {
        return apiService.uploadObject(
            "Bearer $token",
            description
        )
    }

    suspend fun uploadImage(
        file: MultipartBody.Part, token: String
    ): UploadImageResponse {
        return apiService.uploadImage(
            "Bearer $token",
            file
        )
    }

//    suspend fun uploadData(
//        description: RequestBody, token: String
//    )= liveData {
//        emit(ResultState.Loading)
//        try {
//            val successResponse = apiService.uploadObject(
//                "Bearer $token", description)
//            emit(ResultState.Success(successResponse))
//        } catch (e: HttpException) {
//            val errorBody = e.response()?.errorBody()?.string()
//            val errorResponse = Gson().fromJson(errorBody, UploadResponse::class.java)
//            emit(ResultState.Error(errorResponse.message))
//        }
//    }
//
//    suspend fun uploadImage(
//        file: MultipartBody.Part, token: String
//    )= liveData {
//        emit(ResultState.Loading)
//        try {
//            val successResponse = apiService.uploadImage(
//                "Bearer $token", file)
//            emit(ResultState.Success(successResponse))
//        } catch (e: HttpException) {
//            val errorBody = e.response()?.errorBody()?.string()
//            val errorResponse = Gson().fromJson(errorBody, UploadImageResponse::class.java)
//            emit(ResultState.Error(errorResponse.message))
//        }
//    }

    companion object {
        @Volatile
        private var instance: RemoteRepository? = null
        fun getInstance(
            apiService: ApiService,
            context: Context
        ): RemoteRepository = instance ?: synchronized(this) {
            instance ?: RemoteRepository(apiService, context)
        }.also { instance = it }
    }
}