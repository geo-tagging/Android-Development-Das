package com.dicoding.geotaggingjbg.data.response

import com.dicoding.geotaggingjbg.data.database.JenisEntity
import com.dicoding.geotaggingjbg.data.database.KegiatanEntity
import com.dicoding.geotaggingjbg.data.database.LokasiEntity
import com.dicoding.geotaggingjbg.data.database.SkEntity
import com.dicoding.geotaggingjbg.data.database.StatusEntity
import com.google.gson.annotations.SerializedName

data class OptionResponse(

	@field:SerializedName("tb_jenis")
	val tbJenis: List<TbJenisItem>,

	@field:SerializedName("tb_status")
	val tbStatus: List<TbStatusItem>,

	@field:SerializedName("tb_kegiatan")
	val tbKegiatan: List<TbKegiatanItem>,

	@field:SerializedName("tb_action")
	val tbAction: List<TbActionItem>,

	@field:SerializedName("tb_sk")
	val tbSk: List<TbSkItem>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("tb_lokasi")
	val tbLokasi: List<TbLokasiItem>
)

data class TbLokasiItem(

	@field:SerializedName("createdAt")
	val createdAt: Any,

	@field:SerializedName("id_lokasi")
	val idLokasi: Int,

	@field:SerializedName("lokasi")
	val lokasi: String,

	@field:SerializedName("updatedAt")
	val updatedAt: Any
)

data class TbJenisItem(

	@field:SerializedName("createdAt")
	val createdAt: Any,

	@field:SerializedName("nama")
	val nama: String,

	@field:SerializedName("id_jenis")
	val idJenis: Int,

	@field:SerializedName("kategori")
	val kategori: String,

	@field:SerializedName("updatedAt")
	val updatedAt: Any
)

data class TbKegiatanItem(

	@field:SerializedName("createdAt")
	val createdAt: Any,

	@field:SerializedName("kegiatan")
	val kegiatan: String,

	@field:SerializedName("id_kegiatan")
	val idKegiatan: Int,

	@field:SerializedName("updatedAt")
	val updatedAt: Any
)

data class TbSkItem(

	@field:SerializedName("skppkh")
	val skppkh: String,

	@field:SerializedName("createdAt")
	val createdAt: Any,

	@field:SerializedName("id_sk")
	val idSk: Int,

	@field:SerializedName("updatedAt")
	val updatedAt: Any
)

data class TbStatusItem(

	@field:SerializedName("createdAt")
	val createdAt: Any,

	@field:SerializedName("id_status")
	val idStatus: Int,

	@field:SerializedName("status")
	val status: String,

	@field:SerializedName("updatedAt")
	val updatedAt: Any
)

data class TbActionItem(

	@field:SerializedName("createdAt")
	val createdAt: Any,

	@field:SerializedName("action")
	val action: String,

	@field:SerializedName("id_action")
	val idAction: Int,

	@field:SerializedName("updatedAt")
	val updatedAt: Any
)

fun TbJenisItem.toEntity(): JenisEntity {
	return JenisEntity(
		id = this.idJenis,
		nama = this.nama
	)
}
fun TbKegiatanItem.toEntity(): KegiatanEntity {
	return KegiatanEntity(
		id = this.idKegiatan,
		kegiatan = this.kegiatan
	)
}
fun TbLokasiItem.toEntity(): LokasiEntity {
	return LokasiEntity(
		id = this.idLokasi,
		lokasi = this.lokasi
	)
}
fun TbStatusItem.toEntity(): StatusEntity {
	return StatusEntity(
		id = this.idStatus,
		status = this.status
	)
}
fun TbSkItem.toEntity(): SkEntity {
	return SkEntity(
		id = this.idSk,
		sk = this.skppkh
	)
}
