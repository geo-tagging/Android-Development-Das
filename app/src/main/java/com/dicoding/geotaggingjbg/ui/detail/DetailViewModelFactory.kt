package com.dicoding.geotaggingjbg.ui.detail

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.geotaggingjbg.data.pref.UserPreference
import com.dicoding.geotaggingjbg.data.pref.dataStore
import com.dicoding.geotaggingjbg.data.repository.OptionRepository
import com.dicoding.geotaggingjbg.data.repository.Repository
import com.dicoding.geotaggingjbg.data.retrofit.ApiConfig
import com.dicoding.geotaggingjbg.di.Injection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.lang.reflect.InvocationTargetException

class DetailViewModelFactory(private val repository: Repository?, private val optionRepository: OptionRepository, private val id: Int): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            return modelClass.getConstructor(Repository::class.java, OptionRepository::class.java, Int::class.java).newInstance(repository, optionRepository, id)
        } catch (e: InstantiationException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        }
    }

    companion object {
        fun createFactory(activity: Activity, id: Int): DetailViewModelFactory {
            val context = activity.applicationContext ?: throw IllegalStateException("Not yet attached to Application")

            return DetailViewModelFactory(Injection.provideRepository(context), Injection.provideOptionRepository(context), id)
        }
    }
}