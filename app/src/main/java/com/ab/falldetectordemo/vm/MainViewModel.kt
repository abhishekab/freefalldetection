package com.ab.falldetectordemo.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ab.falldetector.FreeFallDetector
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    init {
        FreeFallDetector.init(application)
    }

    val isFallDetectionRunning = FreeFallDetector.getIsFallDetectionRunning()

    val allFalls = FreeFallDetector.allFalls()

    fun startFallDetection() {
        FreeFallDetector.startDetection(threshold = 1.5)
    }

    fun stopFallDetection() {
        FreeFallDetector.stopDetection()
    }

    fun clearData() {
        viewModelScope.launch {
            FreeFallDetector.clear()
        }
    }


}