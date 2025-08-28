package com.example.my_flashlight_app

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest


class MainActivity : AppCompatActivity() {

    private lateinit var toggleButton: Button
    private var isFlashOn = false
    private var cameraId: String? = null
    private lateinit var cameraManager: CameraManager
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>



    //Torch ke state changes ko app ke UI ke sath sync kaise rakhenge?
    //
    //A: CameraManager.TorchCallback register karna hoga.
  private  val torchCallback = object : CameraManager.TorchCallback(){
        override fun onTorchModeChanged(id: String, enabled: Boolean) {
            if(id == cameraId) {
                isFlashOn = enabled
                runOnUiThread{
                    updateUi()
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        toggleButton = findViewById(R.id.toggleButton)
        //cameramanager class to on/off camera
         cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        cameraId = findCameraWithFlash()



//        Agar user ne Camera permission deny kar di to?
//        A: Modern tarike se ActivityResultContracts.RequestPermission() use karenge.
        
         requestPermissionLauncher =
             registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                 granted ->
                 if(granted){
                     toggleFlashLight()
                 }
                 else {
                     Toast.makeText(this,"Camera permission is required",Toast.LENGTH_SHORT).show()
                 }
             }









        
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        else {
            toggleFlashLight()
        }



        toggleButton.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                !=PackageManager.PERMISSION_GRANTED){
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else {
                toggleFlashLight()
            }
        }






        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    //fun to toggle flashlight
   private fun toggleFlashLight() {
        cameraId?.let { id ->
            val newState = !isFlashOn
            cameraManager.setTorchMode(id,newState)
            isFlashOn = newState
            updateUi()
        }

    }


    //to update ui on flash on/off
    private fun updateUi(){
        toggleButton.text = if(isFlashOn) "Turn Off" else "Turn ON"
}

    //fin camera with flash

    private fun findCameraWithFlash():String? {
        for (id in cameraManager.cameraIdList) {
            val chars = cameraManager.getCameraCharacteristics(id)
            if(chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)==true){
                return id
            }
        }
        return null
    }





    //lifecycle
//    onResume → Torch state listen karni chahiye.
//
//    onPause → Torch off karna (agar demo chahiye).
//
//    onDestroy → Ensure karna ki torch band ho jaye.


    override fun onPause() {
        super.onPause()
        if(isFlashOn) {
            cameraManager.setTorchMode(cameraId!!,false)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if(isFlashOn) cameraManager.setTorchMode(cameraId!!,false)

    }

    override fun onResume() {
        super.onResume()
        cameraManager.registerTorchCallback(torchCallback,null)

    }
}





//package com.example.flashlight
//
//import android.Manifest
//import android.content.Context
//import android.content.pm.PackageManager
//import android.hardware.camera2.CameraAccessException
//import android.hardware.camera2.CameraCharacteristics
//import android.hardware.camera2.CameraManager
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import android.widget.Button
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//
//class MainActivity : AppCompatActivity() {
//
//    private val TAG = "MainActivity"
//    private lateinit var cameraManager: CameraManager
//    private var cameraId: String? = null
//    private var isFlashOn = false
//    private lateinit var toggleButton: Button
//
//    // If true -> keep torch on when app goes to background. For lifecycle demo set false.
//    private val keepTorchOnWhenBackground = false
//
//    // Permission launcher (modern API)
//    private val requestPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
//            if (granted) {
//                // user granted permission -> perform the action they requested
//                toggleFlashlight()
//            } else {
//                Toast.makeText(this, "Camera permission is required to use the flashlight", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    private val torchCallback = object : CameraManager.TorchCallback() {
//        override fun onTorchModeChanged(id: String, enabled: Boolean) {
//            // Only care about our cameraId
//            if (id == cameraId) {
//                isFlashOn = enabled
//                runOnUiThread { updateUi() }
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        toggleButton = findViewById(R.id.toggleButton)
//        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
//        cameraId = findCameraWithFlash()
//
//        toggleButton.setOnClickListener {
//            toggleFlashlight()
//        }
//
//        updateUi()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        // register to listen to hardware torch state changes
//        cameraManager.registerTorchCallback(torchCallback, Handler(Looper.getMainLooper()))
//    }
//
//    override fun onPause() {
//        // unregister callback
//        cameraManager.unregisterTorchCallback(torchCallback)
//
//        // demonstrate lifecycle: optionally turn off flashlight when leaving foreground
//        if (!keepTorchOnWhenBackground && isFlashOn) {
//            cameraId?.let {
//                try {
//                    cameraManager.setTorchMode(it, false)
//                } catch (e: CameraAccessException) {
//                    Log.e(TAG, "Failed to turn off torch in onPause: $e")
//                } catch (e: IllegalArgumentException) {
//                    Log.e(TAG, "CameraId was invalid: $e")
//                }
//            }
//        }
//        super.onPause()
//    }
//
//    override fun onDestroy() {
//        // ensure torch is off
//        if (isFlashOn) {
//            cameraId?.let {
//                try {
//                    cameraManager.setTorchMode(it, false)
//                } catch (e: Exception) {
//                    Log.e(TAG, "Error turning off flashlight in onDestroy: $e")
//                }
//            }
//        }
//        super.onDestroy()
//    }
//
//    private fun updateUi() {
//        toggleButton.text = if (isFlashOn) "Turn OFF" else "Turn ON"
//        toggleButton.contentDescription = if (isFlashOn) "Turn flashlight off" else "Turn flashlight on"
//    }
//
//    private fun toggleFlashlight() {
//        val id = cameraId
//        if (id == null) {
//            Toast.makeText(this, "No flashlight available on this device", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // Check permission
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            // Request permission; when user responds the launcher will call back
//            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
//            return
//        }
//
//        try {
//            val newState = !isFlashOn
//            // setTorchMode is immediate; TorchCallback will also update isFlashOn but we optimistically set it here
//            cameraManager.setTorchMode(id, newState)
//            // optimistic update (TorchCallback should update final state)
//            isFlashOn = newState
//            updateUi()
//        } catch (e: CameraAccessException) {
//            Log.e(TAG, "CameraAccessException while toggling flashlight: $e")
//            Toast.makeText(this, "Could not toggle flashlight: ${e.message}", Toast.LENGTH_SHORT).show()
//        } catch (e: IllegalArgumentException) {
//            Log.e(TAG, "Invalid camera id: $e")
//            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun findCameraWithFlash(): String? {
//        return try {
//            for (id in cameraManager.cameraIdList) {
//                val chars = cameraManager.getCameraCharacteristics(id)
//                val hasFlash = chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
//                if (hasFlash) return id
//            }
//            null
//        } catch (e: CameraAccessException) {
//            Log.e(TAG, "CameraAccessException while enumerating cameraIdList: $e")
//            null
//        }
//    }
//}
