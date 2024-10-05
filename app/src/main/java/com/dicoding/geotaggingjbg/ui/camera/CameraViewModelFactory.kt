package com.dicoding.geotaggingjbg.ui.camera

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.geotaggingjbg.data.repository.Repository
import com.dicoding.geotaggingjbg.di.Injection

class CameraViewModelFactory private constructor(
    private val repository: Repository
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            return CameraViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: CameraViewModelFactory? = null
        fun getInstance(context: Context): CameraViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: CameraViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}