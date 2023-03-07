package com.example.sensors

import android.content.Context
import java.util.ArrayList
import android.content.res.AssetManager
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.*
import kotlin.Comparator
import android.os.Handler
import android.os.Looper
import android.os.Message


class ModelFacade private constructor(context: Context) {

    private val assetManager: AssetManager = context.assets
				private var accelerometer: AccelerometerSensorManager = AccelerometerSensorManager(context)
				private var stepCounter: StepCounterSensorManager = StepCounterSensorManager(context)
  var currentSensor = SensorVO ()	
var res = ""


    init {
    	//init
	}

    companion object {
        private var instance: ModelFacade? = null
        fun getInstance(context: Context): ModelFacade {
            return instance ?: ModelFacade(context)
        }
    }
    
	fun classify(x: ArrayList<FloatArray>): String {
        lateinit var tflite : Interpreter
        lateinit var tflitemodel : ByteBuffer

        try{
            tflitemodel = loadModelFile(assetManager, "converted_HARmodel.tflite")
            tflite = Interpreter(tflitemodel)
        } catch(ex: Exception){
            ex.printStackTrace()
        }

        val byteBuffer = ByteBuffer.allocateDirect(4 * 26 * 3 )
        for (j in x) {
        	byteBuffer.putFloat(j[1]/4000)
        	byteBuffer.putFloat(j[1]/4000)
        	byteBuffer.putFloat(j[1]/4000)
        }
        
        val labelsList : List<String> = listOf ("Stationary","Walking","Running")
        val outputVal: ByteBuffer = ByteBuffer.allocateDirect(12)
        outputVal.order(ByteOrder.nativeOrder())
        tflite.run(byteBuffer, outputVal)
        outputVal.rewind()
        
		val output = FloatArray(3)
		for (i in 0..2) {
		    output[i] = outputVal.float
		}

	    res = getSortedResult(output, labelsList).get(0).toString()
        return res
    }
    
		    data class Recognition(
		            var id: String = "",
		            var title: String = "",
		            var confidence: Float = 0F
		        )  {
		            override fun toString(): String {
		                return "$title ($confidence%)"
		            }
		        }
		    
		private fun getSortedResult(labelProbArray: FloatArray, labelList: List<String>): List<Recognition> {
		    
		    val pq = PriorityQueue(
		        labelList.size,
		        Comparator<Recognition> {
		              (_, _, confidence1), (_, _, confidence2)
		              -> confidence1.compareTo(confidence2) * -1
		        })
		    
		    for (i in labelList.indices) {
		        val confidence = labelProbArray[i]
		        pq.add(
		        Recognition("" + i,
		         	if (labelList.size > i) labelList[i] else "Unknown", confidence*100))
		    }
		    val recognitions = ArrayList<Recognition>()
		    val recognitionsSize = Math.min(pq.size, labelList.size)
		    
		    if (pq.size != 0) {
		       for (i in 0 until recognitionsSize) {
		           recognitions.add(pq.poll())
		       }
		    }
		    else {
		           recognitions.add(Recognition("0", "Unknown",100F))
		         }
		    return recognitions
		}
		        
		private fun loadModelFile(assetManager: AssetManager, modelPath: String): ByteBuffer {
		        val fileDescriptor = assetManager.openFd(modelPath)
		        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
		        val fileChannel = inputStream.channel
		        val startOffset = fileDescriptor.startOffset
		        val declaredLength = fileDescriptor.declaredLength
		        return fileChannel.map(
		            FileChannel.MapMode.READ_ONLY,
		            startOffset, declaredLength)
		}
	

    fun collectSensor(): String {
	      var result = ""
	return result
	}
	          


	
	fun startSensors () {
	        if (!accelerometer.sensorExists()){
							            currentSensor.setAccelerometer("No Accelerometer Sensor")
							        }
							        else {
							            accelerometer.startSensor()
							        }
        if (!stepCounter.sensorExists()){
				            currentSensor.setStepCounter("No StepCounter Sensor")
				        }
				        else {
				            stepCounter.startSensor()
				        }
		}        
	
	fun stopSensors () {
	            if (accelerometer.sensorExists()){
					accelerometer.stopSensor()
				}
            if (stepCounter.sensorExists()){
		stepCounter.stopSensor()
	}
		}
	
	fun setHandler() {
	        accelerometer.setHandler(handler)
        stepCounter.setHandler(handler)
		}
	
	    	// Handle messages
	        private val handler: Handler = object : Handler(Looper.getMainLooper()) {
			/*
	         * handleMessage() defines the operations to perform when
	         * the Handler receives a new Message to process.
	         */
			override fun handleMessage(inputMessage: Message) {
				// Gets the image task from the incoming Message object.
				val sensorEvent = inputMessage.obj as MySensorEvent
				val accelerometerArray: ArrayList<FloatArray> = ArrayList()
	
	if (sensorEvent.type == SensorType.ACCELEROMETER){
				   currentSensor.setAccelerometer(sensorEvent.value)

			accelerometerArray.add(sensorEvent.dataAccelerometer)
		classify(accelerometerArray)
		accelerometerArray.clear()
	}        else
if (sensorEvent.type == SensorType.STEP_COUNTER){
				   currentSensor.setStepCounter(sensorEvent.value)

			}        
			}
		}
	
	    fun listSensors(): SensorVO {
	        return currentSensor
	    }
	    
	
}
