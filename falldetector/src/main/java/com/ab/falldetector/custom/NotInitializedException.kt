package com.ab.falldetector.custom

class NotInitializedException :
    Exception("Not Allowed!, you must call FreeFallDetector.init() first")