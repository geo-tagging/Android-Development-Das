package com.dicoding.geotaggingjbg.ui.save

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.geotaggingjbg.data.repository.Repository
import com.dicoding.geotaggingjbg.data.database.Entity
import com.dicoding.geotaggingjbg.data.database.JenisEntity
import com.dicoding.geotaggingjbg.data.database.KegiatanEntity
import com.dicoding.geotaggingjbg.data.database.LokasiEntity
import com.dicoding.geotaggingjbg.data.database.SkEntity
import com.dicoding.geotaggingjbg.data.database.StatusEntity
import com.dicoding.geotaggingjbg.data.repository.OptionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SaveViewModel(private val repository: Repository, private val optionRepository: OptionRepository) : ViewModel() {

    fun saveImageLocal(entity: Entity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveImageToLocal(entity)
        }
    }
}