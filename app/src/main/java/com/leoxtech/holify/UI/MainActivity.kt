package com.leoxtech.holify.UI

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.leoxtech.holify.Common.CheckConnectionLiveData
import com.leoxtech.holify.R

class MainActivity : AppCompatActivity() {

    private lateinit var cld : CheckConnectionLiveData

    private lateinit var fusedLocation: FusedLocationProviderClient
    var isPermissionGranted: Boolean = false
    private val LOCATION_REQUEST_CODE = 100

    private lateinit var requestLauncher: ActivityResultLauncher<String>
    var isNotificationPermissionGranted: Boolean = false

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        fusedLocation = LocationServices.getFusedLocationProviderClient(this)

        // Initialize the requestLauncher with the registerForActivityResult() method.
        requestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // If the permission is granted, display the toast.
                isNotificationPermissionGranted = true
                checkLocationPermission()
            } else {
                // If the permission is not granted, display the toast.
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        notificationPermission()

    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun notificationPermission() {
        // Check if the notification permission is granted or not.
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // If the notification permission is not granted, request for the same.
            requestLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // If the notification permission is already granted, display the toast.
            isNotificationPermissionGranted = true
            checkLocationPermission()
        }
    }

    private fun goToDashboard() {

        cld = CheckConnectionLiveData(application)

        cld.observe(this) {
            if (it) {
                Handler().postDelayed({
                    val intent = Intent(this, Dashboard::class.java)
                    startActivity(intent)
                    finish()
                }, 2000)
            } else {
                Toast.makeText(this,"Not Connected",Toast.LENGTH_SHORT).show()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_REQUEST_CODE)
        } else{
            isPermissionGranted = true
            goToDashboard()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        isPermissionGranted = false
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isPermissionGranted = true
                    goToDashboard()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}