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

    //find camera with flash

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





