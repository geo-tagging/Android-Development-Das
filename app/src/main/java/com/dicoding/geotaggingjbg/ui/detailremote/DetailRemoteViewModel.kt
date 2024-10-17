package com.dicoding.geotaggingjbg.ui.detailremote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.geotaggingjbg.data.repository.RemoteRepository
import com.dicoding.geotaggingjbg.data.repository.Repository
import com.dicoding.geotaggingjbg.data.database.Entity
import com.dicoding.geotaggingjbg.data.database.JenisEntity
import com.dicoding.geotaggingjbg.data.database.KegiatanEntity
import com.dicoding.geotaggingjbg.data.database.LokasiEntity
import com.dicoding.geotaggingjbg.data.database.PetakEntity
import com.dicoding.geotaggingjbg.data.database.RemoteEntity
import com.dicoding.geotaggingjbg.data.database.SkEntity
import com.dicoding.geotaggingjbg.data.database.SkKerjaEntity
import com.dicoding.geotaggingjbg.data.database.StatusAreaTanamEntity
import com.dicoding.geotaggingjbg.data.database.StatusEntity
import com.dicoding.geotaggingjbg.data.repository.OptionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailRemoteViewModel(
    private val repository: Repository,
    private val remoteRepository: RemoteRepository,
    private val optionRepository: OptionRepository,
    id: Int
): ViewModel() {
    private val _detailUser = MutableLiveData<RemoteEntity>()
    var detailUser: LiveData<RemoteEntity> = _detailUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val getRemoteData: LiveData<RemoteEntity> = remoteRepository.getById(id)
    val getLocalData: LiveData<Entity> = repository.getById(id)

    fun saveLocal(entity: Entity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveImageToLocal(entity)
        }
    }

    private val _jenisList = MutableLiveData<List<JenisEntity>>()
    val jenisList: LiveData<List<JenisEntity>> get() = _jenisList

    private val _lokasiList = MutableLiveData<List<LokasiEntity>>()
    val lokasiList: LiveData<List<LokasiEntity>> get() = _lokasiList

    private val _kegiatanList = MutableLiveData<List<KegiatanEntity>>()
    val kegiatanList: LiveData<List<KegiatanEntity>> get() = _kegiatanList

    private val _statusList = MutableLiveData<List<StatusEntity>>()
    val statusList: LiveData<List<StatusEntity>> get() = _statusList

    private val _skList = MutableLiveData<List<SkEntity>>()
    val skList: LiveData<List<SkEntity>> get() = _skList

    private val _skKerjaList = MutableLiveData<List<SkKerjaEntity>>()
    val skKerjaList: LiveData<List<SkKerjaEntity>> get() = _skKerjaList

    private val _statusAreaTanamList = MutableLiveData<List<StatusAreaTanamEntity>>()
    val statusAreaTanamEntity: LiveData<List<StatusAreaTanamEntity>> get() = _statusAreaTanamList

    private val _petakList = MutableLiveData<List<PetakEntity>>()
    val petakList: LiveData<List<PetakEntity>> get() = _petakList

    // Fungsi untuk memuat semua data dari database
    fun loadAllDataFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val jenisList = optionRepository.getJenis()
                val lokasiList = optionRepository.getLokasi()
                val kegiatanList = optionRepository.getKegiatan()
                val statusList = optionRepository.getStatus()
                val skList = optionRepository.getSk()
                val skKerjaList = optionRepository.getSkKerja()
                val statusAreaTanamList = optionRepository.getStatusAreaTanam()
                val petakList = optionRepository.getPetak()

                // Update data di LiveData di UI thread
                withContext(Dispatchers.Main) {
                    _jenisList.value = if (jenisList.isNotEmpty()) jenisList else emptyList()
                    _lokasiList.value = if (lokasiList.isNotEmpty()) lokasiList else emptyList()
                    _kegiatanList.value = if (kegiatanList.isNotEmpty()) kegiatanList else emptyList()
                    _statusList.value = if (statusList.isNotEmpty()) statusList else emptyList()
                    _skList.value = if (skList.isNotEmpty()) skList else emptyList()
                    _skKerjaList.value = if (skKerjaList.isNotEmpty()) skKerjaList else emptyList()
                    _statusAreaTanamList.value = if (statusAreaTanamList.isNotEmpty()) statusAreaTanamList else emptyList()
                    _petakList.value = if (petakList.isNotEmpty()) petakList else emptyList()
                }
            } catch (e: Exception) {
                Log.e("SaveViewModel", "Error loading data from database", e)
            }
        }
    }
}