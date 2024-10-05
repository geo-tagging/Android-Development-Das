package com.dicoding.geotaggingjbg.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.geotaggingjbg.data.repository.RemoteRepository
import com.dicoding.geotaggingjbg.data.database.RemoteEntity

class CameraRemoteViewModel (
    private val remoteRepository: RemoteRepository
) : ViewModel() {
    fun cekIdRemote(id: Int): LiveData<RemoteEntity> {
        return remoteRepository.getById(id)
    }
}