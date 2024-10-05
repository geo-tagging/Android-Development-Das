package com.dicoding.geotaggingjbg.data.repository

import androidx.lifecycle.liveData
import com.dicoding.geotaggingjbg.data.pref.LoginRequest
import com.dicoding.geotaggingjbg.data.pref.UserModel
import com.dicoding.geotaggingjbg.data.pref.UserPreference
import com.dicoding.geotaggingjbg.data.response.LoginResponse
import com.dicoding.geotaggingjbg.data.retrofit.ApiService
import com.dicoding.geotaggingjbg.ui.utils.ResultState
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepository(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {
    fun login(email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse = apiService.login(LoginRequest(email, password))
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = errorBody?.let {
                val errorResponse = Gson().fromJson(it, LoginResponse::class.java)
                errorResponse?.message ?: "Server sedang bermasalah"
            } ?: "Unknown error"
            emit(ResultState.Error(errorMessage))
        } catch (e: Throwable) {
            emit(ResultState.Error("An unknown error occurred"))
        }
    }
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository = instance ?: synchronized(this) {
            instance ?: UserRepository(userPreference, apiService)
        }.also { instance = it }
    }
}