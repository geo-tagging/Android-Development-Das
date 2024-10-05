package com.dicoding.geotaggingjbg.di

import android.app.Application
import android.content.Context
import com.dicoding.geotaggingjbg.data.repository.RemoteRepository
import com.dicoding.geotaggingjbg.data.repository.Repository
import com.dicoding.geotaggingjbg.data.database.AppDatabase
import com.dicoding.geotaggingjbg.data.pref.UserPreference
import com.dicoding.geotaggingjbg.data.pref.dataStore
import com.dicoding.geotaggingjbg.data.repository.OptionRepository
import com.dicoding.geotaggingjbg.data.repository.UserRepository
import com.dicoding.geotaggingjbg.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

object Injection {
    fun provideRepository(context: Context): Repository {
        val database = AppDatabase.getInstance(context)
        val dao = database.dao()
        val executorService = Executors.newSingleThreadExecutor()
        return Repository.getInstance(context.applicationContext as Application)
    }
    fun provideRemoteRepository(context: Context): RemoteRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val database = AppDatabase.getInstance(context)
        val api = ApiConfig.getApiService(user.token)
        return RemoteRepository.getInstance(api, context)
    }
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(pref, apiService)
    }
    fun provideOptionRepository(context: Context): OptionRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val database = AppDatabase.getInstance(context)
        val api = ApiConfig.getApiService(user.token)
        return OptionRepository.getInstance(api, context)
    }
}