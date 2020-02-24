package com.ab.falldetector

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.ab.falldetector.custom.NoAccelerometerException
import com.ab.falldetector.custom.NotInitializedException
import com.ab.falldetector.db.AppDatabase
import com.ab.falldetector.model.Fall
import com.ab.falldetector.repo.FallRepository
import com.ab.falldetector.service.FreeFallDetectorService


object FreeFallDetector {
    private var context: Context? = null
    private var fallRepository: FallRepository? = null

    @Throws(NoAccelerometerException::class)
    fun init(
        context: Context
    ) {
        this.context = context
        val sensorManager = this.context!!.getSystemService(Context.SENSOR_SERVICE)
                as SensorManager
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) {
            throw NoAccelerometerException()
        }
        this.fallRepository = AppDatabase.getDatabase(this.context!!).getFallRepository()
    }

    @Throws(NotInitializedException::class)
    fun startDetection(
        threshold: Double = FreeFallDetectorService.DEFAULT_FREE_FALL_THRESHOLD,
        sensorDelay: Int = FreeFallDetectorService.DEFAULT_SENSOR_DELAY
    ) {
        if (context == null) throw NotInitializedException()
        val serviceIntent = Intent(context, FreeFallDetectorService::class.java)
            .apply {
                putExtra(FreeFallDetectorService.EXTRA_KEY_THRESHOLD, threshold)
                putExtra(FreeFallDetectorService.EXTRA_KEY_SENSOR_DELAY, sensorDelay)
            }
        ContextCompat.startForegroundService(context!!, serviceIntent)
    }

    @Throws(NotInitializedException::class)
    fun stopDetection() {
        if (context == null) throw NotInitializedException()
        val serviceIntent = Intent(context, FreeFallDetectorService::class.java)
        context!!.stopService(serviceIntent)
    }

    @Throws(NotInitializedException::class)
    suspend fun clear() {
        if (fallRepository == null) throw NotInitializedException()
        fallRepository!!.clear()
    }

    @Throws(NotInitializedException::class)
    fun allFalls(): LiveData<List<Fall>> {
        if (fallRepository == null) throw NotInitializedException()
        return fallRepository!!.allFalls
    }


}