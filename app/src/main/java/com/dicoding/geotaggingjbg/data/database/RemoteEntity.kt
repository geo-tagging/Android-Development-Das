package com.dicoding.geotaggingjbg.data.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "remote_entity")
@Parcelize
data class RemoteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id_tanaman")
    val id_tanaman: Int = 0,
    @ColumnInfo(name = "jenis")
    var id_jenis: Int = 0,
    @ColumnInfo(name = "kegiatan")
    var id_kegiatan: Int = 0,
    @ColumnInfo(name = "lokasi")
    var id_lokasi: Int = 0,
    @ColumnInfo(name = "petak")
    var id_petak: Int = 0,
    @ColumnInfo(name = "skppkh")
    var id_sk: Int = 0,
    @ColumnInfo(name = "sk_kerja")
    var id_skKerja: Int = 0,
    @ColumnInfo(name = "status")
    var id_status: Int = 0,
    @ColumnInfo(name = "status_area_tanam")
    var id_statusAreaTanam: Int = 0,

    @ColumnInfo(name = "tinggi")
    var tinggi: Double = 0.0,
    @ColumnInfo(name = "diameter")
    var diameter: Double = 0.0,
    @ColumnInfo(name = "tanggal")
    var tanggal_tanam: String? = null,

    @ColumnInfo(name = "latitude")
    var latitude: Double = 0.0,
    @ColumnInfo(name = "longitude")
    var longitude: Double = 0.0,
    @ColumnInfo(name = "elevasi")
    var elevasi: Double = 0.0,
    @ColumnInfo(name = "easting")
    var easting: Double = 0.0,
    @ColumnInfo(name = "northing")
    var northing: Double = 0.0,

    @ColumnInfo(name = "images")
    var images: String? = "",

    @ColumnInfo(name = "id_action")
    var id_action: Int = 0,
    @ColumnInfo(name = "uid")
    var uid: Int = 1,
) : Parcelable