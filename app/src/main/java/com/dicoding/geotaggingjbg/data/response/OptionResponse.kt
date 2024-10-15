package com.dicoding.geotaggingjbg.data.response

import com.dicoding.geotaggingjbg.data.database.JenisEntity
import com.dicoding.geotaggingjbg.data.database.KegiatanEntity
import com.dicoding.geotaggingjbg.data.database.LokasiEntity
import com.dicoding.geotaggingjbg.data.database.PetakEntity
import com.dicoding.geotaggingjbg.data.database.SkEntity
import com.dicoding.geotaggingjbg.data.database.SkKerjaEntity
import com.dicoding.geotaggingjbg.data.database.StatusAreaTanamEntity
import com.dicoding.geotaggingjbg.data.database.StatusEntity
import com.google.gson.annotations.SerializedName

data class OptionResponse(

	@field:SerializedName("tb_majorArea")
	val tbMajorArea: List<TbMajorAreaItem>,

	@field:SerializedName("tb_jenis")
	val tbJenis: List<TbJenisItem>,

	@field:SerializedName("tb_status")
	val tbStatus: List<TbStatusItem>,

	@field:SerializedName("tb_kegiatan")
	val tbKegiatan: List<TbKegiatanItem>,

	@field:SerializedName("tb_petakUkur")
	val tbPetakUkur: List<TbPetakUkurItem>,

	@field:SerializedName("tb_action")
	val tbAction: List<TbActionItem>,

	@field:SerializedName("tb_statusAreaTanam")
	val tbStatusAreaTanam: List<TbStatusAreaTanamItem>,

	@field:SerializedName("tb_sk")
	val tbSk: List<TbSkItem>,

	@field:SerializedName("tb_skKerja")
	val tbSkKerja: List<TbSkKerjaItem>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("tb_lokasi")
	val tbLokasi: List<TbLokasiItem>
)

data class TbPetakUkurItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("id_petak")
	val idPetak: Int,

	@field:SerializedName("petak_ukur")
	val petakUkur: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)

data class TbJenisItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("nama")
	val nama: String,

	@field:SerializedName("id_jenis")
	val idJenis: Int,

	@field:SerializedName("kategori")
	val kategori: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)

data class TbLokasiItem(

	@field:SerializedName("id_major")
	val idMajor: Int,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("id_lokasi")
	val idLokasi: Int,

	@field:SerializedName("lokasi")
	val lokasi: String,

	@field:SerializedName("kecamatan")
	val kecamatan: String,

	@field:SerializedName("region")
	val region: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)

data class TbSkItem(

	@field:SerializedName("skppkh")
	val skppkh: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("id_sk")
	val idSk: Int,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)

data class TbSkKerjaItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("sk_kerja")
	val skKerja: String,

	@field:SerializedName("id_skKerja")
	val idSkKerja: Int,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)

data class TbKegiatanItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("kegiatan")
	val kegiatan: String,

	@field:SerializedName("id_kegiatan")
	val idKegiatan: Int,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)

data class TbStatusItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("id_status")
	val idStatus: Int,

	@field:SerializedName("status")
	val status: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)

data class TbMajorAreaItem(

	@field:SerializedName("id_major")
	val idMajor: Int,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("counter")
	val counter: Int,

	@field:SerializedName("instansi")
	val instansi: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)

data class TbActionItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("action")
	val action: String,

	@field:SerializedName("id_action")
	val idAction: Int,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)

data class TbStatusAreaTanamItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("id_statusAreaTanam")
	val idStatusAreaTanam: Int,

	@field:SerializedName("status_areaTanam")
	val statusAreaTanam: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
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
fun TbPetakUkurItem.toEntity() : PetakEntity {
	return PetakEntity(
		id = this.idPetak,
		petakUkur = this.petakUkur
	)
}
fun TbSkKerjaItem.toEntity() : SkKerjaEntity {
	return SkKerjaEntity(
		id = this.idSkKerja,
		skKerja = this.skKerja
	)
}
fun TbStatusAreaTanamItem.toEntity() : StatusAreaTanamEntity {
	return StatusAreaTanamEntity(
		id = this.idStatusAreaTanam,
		statusAreaTanam = this.statusAreaTanam
	)
}
