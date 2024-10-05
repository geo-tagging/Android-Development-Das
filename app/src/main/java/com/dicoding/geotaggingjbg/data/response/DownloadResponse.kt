package com.dicoding.geotaggingjbg.data.response

import com.google.gson.annotations.SerializedName

data class DownloadResponse(

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("message")
	val message: String
)

data class DataItem(

	@field:SerializedName("id_lokasi")
	val idLokasi: Int,

	@field:SerializedName("latitude")
	val latitude: String,

	@field:SerializedName("id_sk")
	val idSk: Int,

	@field:SerializedName("northing")
	val northing: String,

	@field:SerializedName("id_kegiatan")
	val idKegiatan: Int,

	@field:SerializedName("uid")
	val uid: Int,

	@field:SerializedName("id_status")
	val idStatus: Int,

	@field:SerializedName("tanggal_tanam")
	val tanggalTanam: String,

	@field:SerializedName("diameter")
	val diameter: Double,

	@field:SerializedName("action")
	val action: String,

	@field:SerializedName("longitude")
	val longitude: String,

	@field:SerializedName("skppkh")
	val skppkh: String,

	@field:SerializedName("images")
	val images: String,

	@field:SerializedName("easting")
	val easting: String,

	@field:SerializedName("id_tanaman")
	val idTanaman: Int,

	@field:SerializedName("nama")
	val nama: String,

	@field:SerializedName("date_modified")
	val dateModified: String,

	@field:SerializedName("id_jenis")
	val idJenis: Int,

	@field:SerializedName("lokasi")
	val lokasi: String,

	@field:SerializedName("kegiatan")
	val kegiatan: String,

	@field:SerializedName("elevasi")
	val elevasi: String,

	@field:SerializedName("id_action")
	val idAction: Int,

	@field:SerializedName("tinggi")
	val tinggi: Double,

	@field:SerializedName("status")
	val status: String,

	@field:SerializedName("username")
	val username: String
)
