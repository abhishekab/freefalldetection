package com.ab.falldetector.custom

class NoAccelerometerException : Exception(
    "Can't Detect Fall!, the Device doesn't have an" +
            " accelerometer and shame that we need it to detect the fall for now"
)