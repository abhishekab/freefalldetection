package com.ab.falldetectordemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ab.falldetector.FreeFallDetector

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FreeFallDetector.init(this)
        FreeFallDetector.startDetection(threshold = 0.7, sensorDelay = 3 )
    }

    override fun onDestroy() {
        FreeFallDetector.stopDetection()
        super.onDestroy()
    }
}
