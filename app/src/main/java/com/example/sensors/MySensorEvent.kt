package com.example.sensors

enum class SensorType(i: Int) {
ACCELEROMETER(1), 
STEP_COUNTER(2)
}

class MySensorEvent {
    var type = SensorType.ACCELEROMETER
    var value = ""
    var dataAccelerometer : FloatArray = FloatArray(3)
    var dataStep_counter : FloatArray = FloatArray(1)

}
