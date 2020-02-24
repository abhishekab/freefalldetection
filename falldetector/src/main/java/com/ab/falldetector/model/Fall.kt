package com.ab.falldetector.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Fall(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fallStartTime: Date,
    val fallEndTime: Date,
    val threshold: Double
)