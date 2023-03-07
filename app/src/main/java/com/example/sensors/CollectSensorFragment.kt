	package com.example.sensors
	
	import android.content.Context
	import android.os.*
	import android.widget.*
	import android.view.View
	import android.view.ViewGroup
	import android.view.LayoutInflater
	import androidx.fragment.app.Fragment
	import androidx.lifecycle.MutableLiveData
	import androidx.lifecycle.Observer
	import androidx.lifecycle.lifecycleScope
	import kotlinx.coroutines.MainScope
	import kotlinx.coroutines.delay
	import kotlinx.coroutines.launch
	import android.Manifest
	import android.app.Activity
	import android.content.Intent
	import android.content.pm.PackageManager
	import androidx.core.content.PermissionChecker
	import androidx.core.content.ContextCompat
	
	class CollectSensorFragment : Fragment(), View.OnClickListener {
		lateinit var root: View
		lateinit var myContext: Context
		private lateinit var model: ModelFacade
	
		private lateinit var accelerometerSensorValue : TextView
		private lateinit var step_counterSensorValue : TextView
		val permissionRequestCode = 100
		val permissionGranted = PackageManager.PERMISSION_GRANTED
		val activityPermission = Manifest.permission.ACTIVITY_RECOGNITION
	
		private lateinit var buttonStart : Button
		private lateinit var buttonStop : Button
		private var start = true
	
		companion object {
			fun newInstance(c: Context): CollectSensorFragment  {
				val fragment = CollectSensorFragment ()
				val args = Bundle()
				fragment.arguments = args
				fragment.myContext = c
				return fragment
			}
		}
	
		override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
			root = inflater.inflate(R.layout.collectsensor_layout , container, false)
			super.onViewCreated(root, savedInstanceState)
	
			model = ModelFacade.getInstance(myContext)
			model.setHandler()
	
			//UI components declaration
			accelerometerSensorValue  = root.findViewById(R.id.accelerometerSensorValue)
		step_counterSensorValue  = root.findViewById(R.id.step_counterSensorValue)
	
			buttonStart = root.findViewById(R.id.buttonStart)
			buttonStart.setOnClickListener(this)
	
			buttonStop = root.findViewById(R.id.buttonStop)
			buttonStop.setOnClickListener(this)
				
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
					requestPermissions(
						arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), permissionRequestCode)
				}
			return root
		}
	
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
			super.onActivityResult(requestCode, resultCode, data)
			if (requestCode == permissionRequestCode) {
				if (resultCode == Activity.RESULT_CANCELED) {
					Toast.makeText(requireContext(), "Steps can't count!", Toast.LENGTH_SHORT).show()
				}
			}
		}
	
		private fun isPermissionGranted(): Boolean{
			return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PermissionChecker.checkSelfPermission(
				requireContext(),
				activityPermission
			) == permissionGranted
			else return true
	
		}
	
		private fun requestactivityPermission(){
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
				requestPermissions(arrayOf(activityPermission), permissionRequestCode)
			}
		}
		override fun onClick(v: View?) {
	
			when (v?.id) {
				R.id.buttonStart-> {
				requestactivityPermission()
					start()
				}
				R.id.buttonStop-> {
					stop()
				}
			}
		}
	
		private fun start () {
		if(!isPermissionGranted()){
				requestactivityPermission()
				return
			}
			model.startSensors()
			start = true
	
			viewLifecycleOwner.lifecycleScope.launchWhenStarted {
				while (start) {
				delay(100)
							accelerometerSensorValue.text = model.listSensors().getAccelerometer()
				step_counterSensorValue.text = model.listSensors().getStepCounter()
				}
		    }
		}
	
		private fun stop () {
			start = false
			model.stopSensors()
			accelerometerSensorValue.text = ""
		step_counterSensorValue.text = ""
		}
	}
