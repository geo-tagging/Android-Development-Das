package com.dicoding.geotaggingjbg.data.retrofit

import com.dicoding.geotaggingjbg.data.database.RemoteEntity
import com.dicoding.geotaggingjbg.data.pref.LoginRequest
import com.dicoding.geotaggingjbg.data.response.DownloadResponse
import com.dicoding.geotaggingjbg.data.response.LoginResponse
import com.dicoding.geotaggingjbg.data.response.OptionResponse
import com.dicoding.geotaggingjbg.data.response.UploadImageResponse
import com.dicoding.geotaggingjbg.data.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import java.sql.Date

interface ApiService {
    @GET("approve/search")
    fun getDatabyLocation(
        @Header("Authorization") token: String,
        @Query("keyword")keyword: String,
        @Query("orderBy")orderBy: String = "lokasi",
        @Query("sortBy")sortBy: String = "ASC",
    ): Call<DownloadResponse>

    @GET("option")
    fun getOption(
        @Header("Authorization") token: String
    ): Call<OptionResponse>

    @Headers("Content-Type: application/json")
    @POST("verification")
    suspend fun uploadObject(
        @Header("Authorization") token: String,
        @Body message: RequestBody
    ): UploadResponse

    @Multipart
    @POST("image/uploads")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): UploadImageResponse

    @Headers("Content-Type: application/json")
    @POST("user/loginUser")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse
}