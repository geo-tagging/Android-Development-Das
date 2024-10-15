package com.dicoding.geotaggingjbg.data.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.sql.Date

@Entity(tableName = "geo_db")
@Parcelize
data class Entity(
    @PrimaryKey
    @ColumnInfo(name = "idTanaman")
    val id: Int = 0,
    @ColumnInfo(name = "image")
    var image: String? = null,

    @ColumnInfo(name = "jenTan")
    var jenTan: Int = 0,
    @ColumnInfo(name = "lokasi")
    var lokasi: Int = 0,
    @ColumnInfo(name = "kegiatan")
    var kegiatan: Int = 0,
    @ColumnInfo(name = "petak")
    var petak: Int = 0,
    @ColumnInfo(name = "sk")
    var sk: Int = 0,
    @ColumnInfo(name = "skKerja")
    var skKerja: Int = 0,
    @ColumnInfo(name = "status")
    var status: Int = 0,
    @ColumnInfo(name = "statusAreaTanam")
    var statusAreaTanam: Int = 0,

    @ColumnInfo(name = "tanggal")
    var tanggal: String? = null,
    @ColumnInfo(name = "tanggalModified")
    var tanggalModified: String? = null,
    @ColumnInfo(name = "tinggi")
    var tinggi: Double = 0.0,
    @ColumnInfo(name = "diameter")
    var diameter: Double = 0.0,

    @ColumnInfo(name = "longitude")
    var longitude: Double = 0.0,
    @ColumnInfo(name = "latitude")
    var latitude: Double = 0.0,
    @ColumnInfo(name = "elevasi")
    var elevasi: Double = 0.0,
    @ColumnInfo(name = "easting")
    var easting: Double = 0.0,
    @ColumnInfo(name = "northing")
    var northing: Double = 0.0,

    @ColumnInfo(name = "verif")
    val verif: Int = 0,
) : Parcelable
