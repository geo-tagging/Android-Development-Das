package com.dicoding.geotaggingjbg.ui.detailremote

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.geotaggingjbg.data.repository.OptionRepository
import com.dicoding.geotaggingjbg.data.repository.RemoteRepository
import com.dicoding.geotaggingjbg.data.repository.Repository
import com.dicoding.geotaggingjbg.di.Injection
import java.lang.reflect.InvocationTargetException

class DetailRemoteViewModelFactory private constructor(
    private val repository: Repository,
    private val remoteRepository: RemoteRepository,
    private val optionRepository: OptionRepository,
    private val id: Int
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailRemoteViewModel::class.java)) {
            return DetailRemoteViewModel(remoteRepository, optionRepository, repository, id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: DetailRemoteViewModelFactory? = null

        fun getInstance(context: Context, id: Int): DetailRemoteViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: DetailRemoteViewModelFactory(
                    Injection.provideRepository(context),
                    Injection.provideRemoteRepository(context),
                    Injection.provideOptionRepository(context),
                    id
                )
            }.also { instance = it }
    }
}