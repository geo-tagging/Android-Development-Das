package com.dicoding.geotaggingjbg.ui.detailremote

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.geotaggingjbg.data.repository.RemoteRepository
import com.dicoding.geotaggingjbg.data.repository.Repository
import com.dicoding.geotaggingjbg.di.Injection
import java.lang.reflect.InvocationTargetException

class DetailRemoteViewModelFactory(
    private val remoteRepository: RemoteRepository?,
    private val repository: Repository?,
    private val id: Int): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            return modelClass.getConstructor(RemoteRepository::class.java, Repository::class.java, Int::class.java)
                .newInstance(remoteRepository, repository, id)
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
            val context = activity.applicationContext
                ?: throw IllegalStateException("Not yet attached to Application")

            val remoteRepository = Injection.provideRemoteRepository(context)
            val repository = Injection.provideRepository(context)

            return DetailRemoteViewModelFactory(remoteRepository, repository, id)
        }
    }
}