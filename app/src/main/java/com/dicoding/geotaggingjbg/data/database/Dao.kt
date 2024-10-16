package com.dicoding.geotaggingjbg.data.database

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface Dao {
    //Entity
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entity: Entity)
    @Update
    fun update(entity: Entity)
    @Query("DELETE FROM geo_db")
    fun delete()
    @Delete
    fun deleteEntity(entity: Entity)
    @Query("SELECT * FROM geo_db")
    fun getAllData(): LiveData<List<Entity>>
    @Query("SELECT * FROM geo_db WHERE idTanaman = :id")
    fun getDatabyId(id:Int): LiveData<Entity>
    @Query("SELECT * FROM geo_db ORDER BY idTanaman DESC")
    fun pagingSource(): PagingSource<Int, Entity>

    //RemoteEntity
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRemote(entity: RemoteEntity)
    @Query("SELECT * FROM remote_entity WHERE id_tanaman = :id")
    fun getDataRemotebyId(id:Int): LiveData<RemoteEntity>
    @Query("DELETE FROM remote_entity")
    fun deleteData()

    //OptionEntity
    @Insert
    suspend fun insertAllJenis(jenis: List<JenisEntity>)
    @Query("SELECT * FROM tb_jenis")
    fun getAllJenis(): List<JenisEntity>
    @Query("DELETE FROM tb_jenis")
    fun deleteJenis()

    @Insert
    suspend fun insertAllKegiatan(kegiatan: List<KegiatanEntity>)
    @Query("SELECT * FROM tb_kegiatan")
    fun getAllKegiatan(): List<KegiatanEntity>
    @Query("DELETE FROM tb_kegiatan")
    fun deleteKegiatan()

    @Insert
    suspend fun insertAllLokasi(lokasi: List<LokasiEntity>)
    @Query("SELECT * FROM tb_lokasi")
    fun getAllLokasi(): List<LokasiEntity>
    @Query("DELETE FROM tb_lokasi")
    fun deleteLokasi()

    @Insert
    suspend fun insertAllStatus(lokasi: List<StatusEntity>)
    @Query("SELECT * FROM tb_status")
    fun getAllStatus(): List<StatusEntity>
    @Query("DELETE FROM tb_status")
    fun deleteStatus()

    @Insert
    suspend fun insertAllSk(lokasi: List<SkEntity>)
    @Query("SELECT * FROM tb_sk")
    fun getAllSk(): List<SkEntity>
    @Query("DELETE FROM tb_sk")
    fun deleteSk()

    @Insert
    suspend fun insertAllSkKerja(lokasi: List<SkKerjaEntity>)
    @Query("SELECT * FROM tb_skKerja")
    fun getAllSkKerja(): List<SkKerjaEntity>
    @Query("DELETE FROM tb_skKerja")
    fun deleteSkKerja()

    @Insert
    suspend fun insertAllStatusAreaTanam(lokasi: List<StatusAreaTanamEntity>)
    @Query("SELECT * FROM tb_status_areaTanam")
    fun getAllStatusAreaTanam(): List<StatusAreaTanamEntity>
    @Query("DELETE FROM tb_status_areaTanam")
    fun deleteStatusAreaTanam()

    @Insert
    suspend fun insertAllPetak(lokasi: List<PetakEntity>)
    @Query("SELECT * FROM tb_petak")
    fun getAllPetak(): List<PetakEntity>
    @Query("DELETE FROM tb_petak")
    fun deletePetak()
}