package com.ab.falldetector.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ab.falldetector.db.dao.FallDao
import com.ab.falldetector.model.Fall
import com.ab.falldetector.repo.FallRepository

@Database(entities = arrayOf(Fall::class), version = 1)
@TypeConverters(RoomTypeConverter::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun fallDao(): FallDao
    internal fun getFallRepository(): FallRepository = FallRepository(fallDao())

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Create a singleton instance of database opening
        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fall_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}