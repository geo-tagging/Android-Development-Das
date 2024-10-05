package com.dicoding.geotaggingjbg.data.response

import com.google.gson.annotations.SerializedName

class UploadResponse(
    @field:SerializedName("message")
    val message: String,
    @field:SerializedName("error")
    val error: Boolean
)