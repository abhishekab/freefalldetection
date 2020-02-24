# Free Fall Detector
An Android Library to detect free fall of the phone. The library implements a very simple algorithm to detect the free fall of a phone
using the accelerometer readings

## Methodology
During a free fall the magnitude of the acceleration vector should approach 0 and when it lands somewhere, a spike in the magnitude should be encountered.
So we can set a threshold magnitude, and when the magnitude drops below that threshhold we can mark the start of the fall, and thereafter when
the magnitude rises back above the threshold, that can be marked as the end of fall. The algorithm in not fully robust, but can easily eliminate the other movements of 
phone like shaking, rotating etc. The algorithm can lead (but not limited) to some false records if a temporary obstruction is placed between the fall or the phone 
is dropped on a elastic surface.

## What the Library Does

- Runs a foreground service which detects the falls which can be started and stopped by the caller
- Stores the fall details in a SQLite database
- Pushes a notification to device at the end of fall
- Publishes the history of falls stored in the DB to be consumed by the caller


## Usage

Call the init() function of FreeFallDetector(singleton object) passing the context. This has to be the very first step before using the library
```
try{
 FreeFallDetector.init(applicationContext)
 }
 catch(ex : NoAccelerometerException){
 // Handle exception
 }
 
```
 
To start the Fall Detection Service call the startDetection method with optional parameters to customize magnitude threshold and sensor delay
```
FreeFallDetector.startDetection(threshold = 1.5, sensorDelay = SensorManager.SENSOR_DELAY_UI)
```
 
To stop the Fall Detection Service call the stopDetection method
```
FreeFallDetector.stopDetection()
```
  
To know the status of whether the service is running or not, you can observe the livedata exposed by 
  
```
FreeFallDetector.getIsFallDetectionRunning(): LiveData<Boolean>
```

To get all the history of the falls, you can observe the livedata exposed by
```
FreeFallDetector.allFalls(): LiveData<List<Fall>>
```

To clear the database, you may call the coroutine method
```
FreeFallDetector.clear()
```

A demo app which uses the functionality of the library is also 
[included](https://github.com/abhishekab/freefalldetection/tree/master/app)
  
  
  
