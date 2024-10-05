package com.dicoding.geotaggingjbg.ui.landing

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.geotaggingjbg.data.database.LokasiEntity
import com.dicoding.geotaggingjbg.data.repository.RemoteRepository
import com.dicoding.geotaggingjbg.data.database.RemoteEntity
import com.dicoding.geotaggingjbg.data.pref.UserModel
import com.dicoding.geotaggingjbg.data.repository.OptionRepository
import com.dicoding.geotaggingjbg.data.repository.UserRepository
import com.dicoding.geotaggingjbg.ui.helper.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LandingViewModel(
    private val repository: RemoteRepository,
    id: Int,
    private val userRepository: UserRepository,
    private val optionRepository: OptionRepository
) : ViewModel() {
    private val _result = MutableLiveData<Result<Unit>>()
    val result: LiveData<Result<Unit>> get() = _result
    val getData: LiveData<RemoteEntity> = repository.getById(id)

    private val _lokasiList = MutableLiveData<List<LokasiEntity>>()
    val lokasiList: LiveData<List<LokasiEntity>> get() = _lokasiList

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun fetchDataAndSaveToDatabase(token: String, keyword: String): Job {
        return viewModelScope.launch {
            try {
                repository.fetchDataAndSaveToDatabase(token, keyword)
                _result.value = Result.Success(Unit)
            } catch (e: Exception) {
                _result.value = Result.Error(e.message ?: "Unknown error", e)
            }
        }
    }

    fun deleteData(){
        getData.value?.let {
            repository.deleteDataFromLocalDatabase()
        }
    }

    fun optionToDatabase(token: String): Job{
        return viewModelScope.launch {
            try {
                optionRepository.optionToDatabase(token)
                _result.value = Result.Success(Unit)
            } catch (e: Exception) {
                _result.value = Result.Error(e.message ?: "Unknown error", e)
            }
        }
    }

    fun loadLokasiFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val lokasiList = optionRepository.getLokasi()  // Dipanggil dari background thread
                withContext(Dispatchers.Main) {
                    _lokasiList.value = lokasiList  // Update ke UI harus di main thread
                }
            } catch (e: Exception) {
                Log.e("LandingViewModel", "Error loading lokasi from database", e)
            }
        }
    }

//    fun allLokasi(){
//        viewModelScope.launch {
//            val lokasi = optionRepository.getLokasi()
//            _lokasiList.value = lokasi
//        }
//    }

}