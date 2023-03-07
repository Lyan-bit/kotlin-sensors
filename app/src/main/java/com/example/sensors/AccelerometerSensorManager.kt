package com.example.sensors

import android.app.Service
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.HandlerThread
import android.util.Log

class AccelerometerSensorManager(myContext: Context) :
    HandlerThread("AccelerometerSensorManager"), SensorEventListener {
    private val TAG: String = "AccelerometerSensorManager"
    private var handler: Handler? = null
    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null
    private var sensorExists = false
    private var sensorThread: HandlerThread? = null
    private var sensorHandler: Handler? = null

    init {
        sensorManager =
            (myContext.applicationContext.getSystemService(Service.SENSOR_SERVICE)) as SensorManager
        sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        // Check sensor exists
        if (sensor != null) {
            sensorExists = true
        } else {
            sensorExists = false
        }
    }

    fun startSensor() {
        sensorThread = HandlerThread(TAG, Thread.NORM_PRIORITY)
        sensorThread!!.start()
        sensorHandler =
            Handler(sensorThread!!.getLooper()) //Blocks until looper is prepared, which is fairly quick
        sensorManager!!.registerListener(
            this,
            sensor, SensorManager.SENSOR_DELAY_NORMAL,
            sensorHandler
        )
    }

    fun stopSensor() {
        sensorManager!!.unregisterListener(this)
        sensorThread!!.quitSafely()
    }

    fun sensorExists(): Boolean {
        return sensorExists
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "" + accuracy)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.values.isNotEmpty()) {

            var msgEvent = MySensorEvent()
            msgEvent.type = SensorType.ACCELEROMETER
            		

            // Format event values
            msgEvent.value =
                "x: " + event.values[0] + "\ny: " + event.values[1] + "\nz: " + event.values[2]

            // Send message to MainActivity
            sendMessage(msgEvent)
        }
    }

    fun setHandler(handler: Handler) {
        this.handler = handler
    }

    fun sendMessage(sensorEvent: MySensorEvent) {
        if (handler == null) return

        handler?.obtainMessage(sensorEvent.type.ordinal, sensorEvent)?.apply {
            sendToTarget()
        }
    }
}

