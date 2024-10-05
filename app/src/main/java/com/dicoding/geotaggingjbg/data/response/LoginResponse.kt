package com.dicoding.geotaggingjbg.data.response

import com.google.gson.annotations.SerializedName

data class LoginResponse (
    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("token")
    val token: String,

    @field:SerializedName("uid")
    val uid: Int
)