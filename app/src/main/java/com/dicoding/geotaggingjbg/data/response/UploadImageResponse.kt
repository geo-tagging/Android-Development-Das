package com.dicoding.geotaggingjbg.data.response

import com.google.gson.annotations.SerializedName

data class UploadImageResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("url")
	val url: String
)
