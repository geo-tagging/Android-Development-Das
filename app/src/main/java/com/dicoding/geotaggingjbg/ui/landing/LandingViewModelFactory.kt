package com.dicoding.geotaggingjbg.ui.landing

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.geotaggingjbg.data.repository.OptionRepository
import com.dicoding.geotaggingjbg.data.repository.RemoteRepository
import com.dicoding.geotaggingjbg.data.repository.UserRepository
import com.dicoding.geotaggingjbg.di.Injection

class LandingViewModelFactory private constructor(private val repository: RemoteRepository, private val id:Int, private val userRepository: UserRepository, private val optionRepository: OptionRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LandingViewModel::class.java)) {
            return LandingViewModel(repository, id, userRepository, optionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: LandingViewModelFactory? = null
        fun getInstance(context: Context, id: Int): LandingViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: LandingViewModelFactory(Injection.provideRemoteRepository(context), id, Injection.provideUserRepository(context), Injection.provideOptionRepository(context))
            }.also { instance = it }
    }
}