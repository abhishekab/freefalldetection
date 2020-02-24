package com.ab.falldetector.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ab.falldetector.model.Fall

@Dao
internal interface FallDao {
    @Query("SELECT * FROM fall ORDER by fallStartTime DESC")
    fun getAll(): LiveData<List<Fall>>

    @Insert
    suspend fun insert(fall: Fall)

    @Query("DELETE FROM fall")
    suspend fun clear()
}