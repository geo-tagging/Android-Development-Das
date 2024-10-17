package com.dicoding.geotaggingjbg.ui.detailremote

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.geotaggingjbg.data.repository.OptionRepository
import com.dicoding.geotaggingjbg.data.repository.RemoteRepository
import com.dicoding.geotaggingjbg.data.repository.Repository
import com.dicoding.geotaggingjbg.di.Injection
import java.lang.reflect.InvocationTargetException

class DetailRemoteViewModelFactory(private val repository: Repository, private val remoteRepository: RemoteRepository, private val optionRepository: OptionRepository, private val id: Int) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            return modelClass.getConstructor(Repository::class.java, RemoteRepository::class.java, OptionRepository::class.java, Int::class.java).newInstance(repository, remoteRepository, optionRepository, id)
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
        fun createFactory(activity: Activity, id: Int): DetailRemoteViewModelFactory {
            val context = activity.applicationContext ?: throw IllegalStateException("Not yet attached to Application")

            return DetailRemoteViewModelFactory(Injection.provideRepository(context), Injection.provideRemoteRepository(context), Injection.provideOptionRepository(context), id)
        }
    }
}