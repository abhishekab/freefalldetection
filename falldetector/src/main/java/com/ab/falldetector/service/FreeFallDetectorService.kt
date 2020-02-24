package com.ab.falldetector.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.ab.falldetector.R
import com.ab.falldetector.custom.NoAccelerometerException
import com.ab.falldetector.db.AppDatabase
import com.ab.falldetector.model.Fall
import com.ab.falldetector.repo.FallRepository
import com.ab.falldetector.util.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sqrt


internal class FreeFallDetectorService : Service(), SensorEventListener {
    companion object {
        const val FOREGROUND_ID = 100001
        const val CHANNEL_FALL_DETECTOR_RUNNING = "channel_fall_detector_running"
        const val CHANNEL_FALL_DETECTED = "channel_fall_detected"
        const val DEFAULT_FREE_FALL_THRESHOLD = 0.7
        const val DEFAULT_SENSOR_DELAY = SensorManager.SENSOR_DELAY_UI
        const val EXTRA_KEY_THRESHOLD = "FreeFallDetectorService.EXTRA_KEY_THRESHOLD"
        const val EXTRA_KEY_SENSOR_DELAY = "FreeFallDetectorService.EXTRA_KEY_SENSOR_DELAY"
    }

    val TAG = FreeFallDetectorService::class.java.simpleName
    private lateinit var sensorManager: SensorManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var fallRepository: FallRepository
    private var accelerometerSensor: Sensor? = null
    private var isInFall = false
    private var fallStartTime = Utils.getNow()
    private var sensorDelay = DEFAULT_SENSOR_DELAY
    private var threshold = DEFAULT_FREE_FALL_THRESHOLD


    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel for foreground service notification
            createNotificationChannel(
                Companion.CHANNEL_FALL_DETECTOR_RUNNING,
                getString(R.string.channel_foreground_service_name),
                getString(R.string.channel_foreground_service_desc),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            // Create channel for pushing notification when fall is detected

            createNotificationChannel(
                CHANNEL_FALL_DETECTED,
                getString(R.string.channel_fall_detected_name),
                getString(R.string.channel_fall_detected_desc),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        }
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometerSensor == null) {
            throw NoAccelerometerException()
        }
        fallRepository = AppDatabase.getDatabase(this).getFallRepository()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        Log.d(TAG, "On Start Command")
        accelerometerSensor?.let {
            isInFall = false
            threshold = intent.getDoubleExtra(EXTRA_KEY_THRESHOLD, DEFAULT_FREE_FALL_THRESHOLD)
            sensorDelay = intent.getIntExtra(EXTRA_KEY_SENSOR_DELAY, DEFAULT_SENSOR_DELAY)
            Log.d(TAG, "Th=$threshold, Sd=$sensorDelay")
            // Unregister old listener if any
            unregisterListener()
            sensorManager.registerListener(this, it, sensorDelay)
            val notification: Notification = NotificationCompat.Builder(
                this,
                CHANNEL_FALL_DETECTOR_RUNNING
            )
                .setContentTitle(getString(R.string.default_notification_fall_detection_running_title))
                .setContentText(getString(R.string.default_notification_fall_detection_running_body))
                .setSmallIcon(R.drawable.ic_small_icon)
                .build()
            startForeground(FOREGROUND_ID, notification)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        unregisterListener()
        stopForeground(true)
        Log.d(TAG, "Destroyed Service")
        super.onDestroy()
    }

    override fun onAccuracyChanged(sensor: Sensor?, var1: Int) {
        // Do Nothing for now
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val xForce = event.values[0]
            val yForce = event.values[1]
            val zForce = event.values[2]
            val magnitude = sqrt(xForce.pow(2) + yForce.pow(2) + zForce.pow(2));
            Log.v(TAG, "Magnitude:$magnitude")

            // The variable isInFall is used to track the period while the phone is encountering
            // free fall. isInFall is set to true when the magnitude falls below the threshold
            // and is set to fals when the magnitude exceeds the threshhold. This is done to
            // ensure that multiple events during the same fall can be clubbed together
            if (magnitude <= threshold && !isInFall) {
                fallStartTime = Utils.getNow()
                Log.d(TAG, "Fall Detected")
                isInFall = true
            } else if (magnitude > threshold && isInFall) {
                isInFall = false
                Log.d(TAG, "Fall ended")
                val fallEndTime = Utils.getNow()
                showFallEndedNotification()
                GlobalScope.launch {
                    fallRepository.insert(
                        Fall(
                            fallStartTime = fallStartTime, fallEndTime = fallEndTime,
                            threshold = threshold
                        )
                    )
                }
            }
        }
    }

    private fun showFallEndedNotification() {
        val notification: Notification = NotificationCompat.Builder(
            this,
            CHANNEL_FALL_DETECTED
        )
            .setContentTitle(getString(R.string.default_notification_fall_detected_title))
            .setContentText(getString(R.string.default_notification_fall_detected_body))
            .setSmallIcon(R.drawable.ic_small_icon)
            .setAutoCancel(true)
            .build()
        // Unique Id
        val notificationId = (System.currentTimeMillis() and 0xfffffff).toInt()
        notificationManager.notify(notificationId, notification)
    }

    private fun unregisterListener() {
        accelerometerSensor?.let {
            sensorManager.unregisterListener(this, it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        channelId: String, channelName: String,
        channelDescription: String, importance: Int
    ) {
        val serviceChannel = NotificationChannel(
            channelId,
            channelName,
            importance
        ).apply { description = channelDescription }

        notificationManager.createNotificationChannel(serviceChannel)
    }


}
