package com.dicoding.geotaggingjbg.ui.camera

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.geotaggingjbg.data.repository.RemoteRepository
import com.dicoding.geotaggingjbg.di.Injection

class CameraRemoteViewModelFactory private constructor(
    private val repository: RemoteRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraRemoteViewModel::class.java)) {
            return CameraRemoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: CameraRemoteViewModelFactory? = null
        fun getInstance(context: Context): CameraRemoteViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: CameraRemoteViewModelFactory(Injection.provideRemoteRepository(context))
            }.also { instance = it }
    }
}