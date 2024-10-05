package com.dicoding.geotaggingjbg.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.geotaggingjbg.data.repository.Repository
import com.dicoding.geotaggingjbg.data.database.Entity

class CameraViewModel(
    private val repository: Repository
) : ViewModel() {
    fun cekId(id: Int): LiveData<Entity>{
        return repository.getById(id)
    }
}