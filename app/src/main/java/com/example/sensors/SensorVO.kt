package com.example.sensors

import java.util.ArrayList

class SensorVO  {

    private var accelerometer: String = ""
    private var stepCounter: String = ""

    constructor() {
    	//constructor
    }

    constructor(accelerometerx: String, stepCounterx: String
        ) {
       this.accelerometer = accelerometerx
       this.stepCounter = stepCounterx
    }

    constructor (x: SensorVO) {
      accelerometer = x.accelerometer
      stepCounter = x.stepCounter
    }

    override fun toString(): String {
        return "accelerometer = $accelerometer, stepCounter = $stepCounter"
    }

    fun toStringList(list: List<SensorVO>): List<String> {
        val res: MutableList<String> = ArrayList()
        for (i in list.indices) {
            res.add(list[i].toString())
        }
        return res
    }

    fun getAccelerometer(): String {
        return accelerometer
	}
		
	fun setAccelerometer(x: String) {
        accelerometer = x
	}
    fun getStepCounter(): String {
        return stepCounter
	}
		
	fun setStepCounter(x: String) {
        stepCounter = x
	}
}

