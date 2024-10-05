package com.dicoding.geotaggingjbg.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.geotaggingjbg.data.repository.Repository
import com.dicoding.geotaggingjbg.data.database.Entity

class DetailViewModel(private val repository: Repository, id: Int): ViewModel() {
    private val _detailUser = MutableLiveData<Entity>()
    var detailUser: LiveData<Entity> = _detailUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val getData: LiveData<Entity> = repository.getById(id)

    fun updateData(entity: Entity){
        repository.update(entity)
    }

    fun deleteData(){
        getData.value?.let {
            repository.deleteEntity(it)
        }
    }
}