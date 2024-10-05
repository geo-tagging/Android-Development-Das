package com.dicoding.geotaggingjbg.data.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiConfig {
    companion object{
        fun getApiService(token: String): ApiService {
            val TIMEOUT = 15000L
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .build()
            val retrofit = Retrofit.Builder()
//                .baseUrl("https://dqscf5fv-4000.asse.devtunnels.ms")/5/alif
//                .baseUrl("https://sw9brdp4-4000.asse.devtunnels.ms/api/")//iky
//                .baseUrl("https://grcbdgfj-4000.asse.devtunnels.ms")//aku
                .baseUrl("https://api-dot-logia-426615.et.r.appspot.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}