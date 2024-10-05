package com.dicoding.geotaggingjbg.ui.home

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.geotaggingjbg.data.repository.RemoteRepository
import com.dicoding.geotaggingjbg.data.repository.Repository
import com.dicoding.geotaggingjbg.data.database.Entity
import com.dicoding.geotaggingjbg.data.pref.UserModel
import com.dicoding.geotaggingjbg.data.repository.UserRepository
import com.dicoding.geotaggingjbg.ui.utils.ResultState
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class HomeViewModel(
    private val repository: Repository,
    private val remoteRepository: RemoteRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    fun getData(): LiveData<List<Entity>> {
        return repository.getAllData()
    }
    fun delete(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete()
        }
    }
    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }
    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
    fun uploadData(description: RequestBody, token:String):LiveData<ResultState<String>> {
        val resultLiveData = MutableLiveData<ResultState<String>>()
        viewModelScope.launch {
            try {
                resultLiveData.value = ResultState.Loading
                remoteRepository.uploadData(
                    description,
                    token
                )
                resultLiveData.value = ResultState.Success("Data berhasil diunggah")
            } catch (e: Exception) {
                resultLiveData.value = ResultState.Error("Gagal mengunggah data: ${e.message}")
                Log.e(TAG, "Error uploading data: ${e.message}", e)
            }
        }
        return resultLiveData
    }
    fun uploadImage(context: Context, file: File, token: String) {
        viewModelScope.launch {
            try {
                val compressedImage = compressImage(context, file)
                val requestBody = compressedImage.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart= MultipartBody.Part.createFormData(
                    "image",
                    file.name,
                    requestBody
                )
                remoteRepository.uploadImage(imageMultipart, token)
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading image: ${e.message}", e)
            }
        }
    }
    fun getPagingData(): LiveData<PagingData<Entity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5, // Jumlah item per halaman
                enablePlaceholders = false // Menentukan apakah placeholder diperbolehkan
            ),
            pagingSourceFactory = { repository.getPagingSource() } // Menggunakan fungsi dari repository
        ).liveData
    }
    private suspend fun compressImage(context: Context, file: File): File {
        return withContext(Dispatchers.IO) {
            Compressor.compress(context, file){
                quality(60)
            }
        }
    }
}