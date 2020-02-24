package com.ab.falldetector.repo

import com.ab.falldetector.db.dao.FallDao
import com.ab.falldetector.model.Fall

internal class FallRepository(private val fallDao: FallDao) {
    val allFalls = fallDao.getAll()
    suspend fun insert(fall: Fall) {
        fallDao.insert(fall)
    }

    suspend fun clear() {
        fallDao.clear()
    }
}