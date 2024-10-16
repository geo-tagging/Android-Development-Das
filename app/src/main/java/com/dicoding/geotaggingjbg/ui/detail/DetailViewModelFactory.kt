package com.dicoding.geotaggingjbg.ui.detail

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.geotaggingjbg.data.repository.OptionRepository
import com.dicoding.geotaggingjbg.data.repository.Repository
import com.dicoding.geotaggingjbg.di.Injection
import com.dicoding.geotaggingjbg.ui.save.SaveViewModel
import com.dicoding.geotaggingjbg.ui.save.SaveViewModelFactory
import java.lang.reflect.InvocationTargetException

class DetailViewModelFactory private constructor(
    private val repository: Repository,
    private val optionRepository: OptionRepository,
    private val id: Int
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(repository, id, optionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: DetailViewModelFactory? = null

        fun getInstance(context: Context, id: Int): DetailViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: DetailViewModelFactory(
                    Injection.provideRepository(context),
                    Injection.provideOptionRepository(context),
                    id
                )
            }.also { instance = it }
    }
}