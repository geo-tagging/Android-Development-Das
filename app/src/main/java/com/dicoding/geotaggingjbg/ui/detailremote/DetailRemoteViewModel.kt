package com.dicoding.geotaggingjbg.ui.detailremote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.geotaggingjbg.data.repository.RemoteRepository
import com.dicoding.geotaggingjbg.data.repository.Repository
import com.dicoding.geotaggingjbg.data.database.Entity
import com.dicoding.geotaggingjbg.data.database.RemoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailRemoteViewModel(private val remoteRepository: RemoteRepository, private val repository: Repository, id: Int): ViewModel() {
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
}