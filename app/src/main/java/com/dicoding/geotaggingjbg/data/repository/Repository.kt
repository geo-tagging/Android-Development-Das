package com.dicoding.geotaggingjbg.data.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.dicoding.geotaggingjbg.data.database.AppDatabase
import com.dicoding.geotaggingjbg.data.database.Dao
import com.dicoding.geotaggingjbg.data.database.Entity
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Repository(context: Context) {
    private var dao: Dao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = AppDatabase.getInstance(context)
        dao = db.dao()
    }

    fun getAllData(): LiveData<List<Entity>> {
        return dao.getAllData()
    }

    fun delete() {
        executorService.execute { dao.delete() }
    }

    fun deleteEntity(entity: Entity) {
        executorService.execute { dao.deleteEntity(entity) }
    }

    fun saveImageToLocal(entity: Entity) {
        executorService.execute { dao.insert(entity) }
    }

    fun update(entity: Entity) {
        Log.d("Repository", "Updating entity: $entity")
        executorService.execute { dao.update(entity) }
    }

    fun getById(id:Int): LiveData<Entity>{
        return dao.getDatabyId(id)
    }

    fun getPagingSource(): PagingSource<Int, Entity> {
        return dao.pagingSource()
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            context: Context
        ): Repository = instance ?: synchronized(this) {
            instance ?: Repository(context)
        }.also { instance = it }
    }
}