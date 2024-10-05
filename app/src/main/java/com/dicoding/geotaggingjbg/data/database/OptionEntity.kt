package com.dicoding.geotaggingjbg.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_jenis")
data class JenisEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val nama: String
)

@Entity(tableName = "tb_kegiatan")
data class KegiatanEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val kegiatan: String
)

@Entity(tableName = "tb_lokasi")
data class LokasiEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val lokasi: String
)

@Entity(tableName = "tb_status")
data class StatusEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val status: String
)

@Entity(tableName = "tb_sk")
data class SkEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val sk: String
)