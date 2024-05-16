package com.dream.friend.ui.location_permissions

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.dream.friend.R
import com.dream.friend.common.PreferenceHandler
import com.dream.friend.common.Utils.dialogLoading
import com.dream.friend.common.Utils.onBackPress
import com.dream.friend.common.Utils.showToast
import com.dream.friend.common.activityBindings
import com.dream.friend.common.startRotationAnimation
import com.dream.friend.data.model.peronal_details.LocationReq
import com.dream.friend.data.model.user.Location
import com.dream.friend.data.network.Resource
import com.dream.friend.databinding.ActivityLocationPermissionBinding
import com.dream.friend.ui.home.HomeActivity
import com.dream.friend.ui.viewModel.UpdateUserDetailsViewModel
import com.dream.friend.ui.viewModel.UserLoginViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationPermissionActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var dialog: Dialog
    private val viewModelUserLogin: UserLoginViewModel by viewModels()
    private val viewModel: UpdateUserDetailsViewModel by viewModels()
    private val locationPermissionBinding: ActivityLocationPermissionBinding by activityBindings(R.layout.activity_location_permission)
    private lateinit var preferenceHandler: PreferenceHandler
    private var locationManager: LocationManager? = null
    private var userLocation: Location? = null
    private var locationRequest: LocationRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(locationPermissionBinding.root)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        onBackPress()

        dialog = dialogLoading()
        dialog.dismiss()
        preferenceHandler = PreferenceHandler(this)

        // locationPermissionBinding.tvNoOtherTime.setOnClickListener(this)
        locationPermissionBinding.btnAllowPermission.setOnClickListener(this)
        locationPermissionBinding.btnAllowPermissionManually.setOnClickListener(this)
        locationPermissionBinding.toolbar.ivBack.setOnClickListener(this)
        locationPermissionBinding.toolbar.tvBack.setOnClickListener(this)
        locationResponse()
    }

    private fun locationResponse() {
        viewModel.updateUserLocationResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let {
                        dialog.dismiss()
                        moveToNextScr()
                    }
                }

                is Resource.Error -> {
                    dialog.dismiss()
                    response.message?.let {
                        showToast(it)
                    }
                }

                is Resource.Loading -> {
                    dialog.show()
                    dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
                }

                is Resource.TokenRenew -> {
                }
            }
        }
    }

    override fun onClick(id: View?) {
        when (id) {
            // locationPermissionBinding.tvNoOtherTime -> moveToNextScr(false)
            locationPermissionBinding.toolbar.tvBack, locationPermissionBinding.toolbar.ivBack -> finish()

            locationPermissionBinding.btnAllowPermission -> {
                if(isLocationPermissionGranted()){
                    storeLocation()
                }else{
                    requestLocationPermission()
                }
            }

            locationPermissionBinding.btnAllowPermissionManually -> {
                startActivity(Intent(this, ManualLocationActivity::class.java))
            }
        }
    }

    private fun requestLocationPermission() = requestLocationPermissionGranted()

    private fun isLocationPermissionGranted() = (ActivityCompat.checkSelfPermission(
        this@LocationPermissionActivity, ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
        this@LocationPermissionActivity, ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED)

    private fun requestLocationPermissionGranted() =
        ActivityCompat.requestPermissions(this@LocationPermissionActivity,locationPermissionStrings,1001)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==1001){
            if(isLocationPermissionGranted()){
              storeLocation()
            }
        }
    }

    private fun moveToNextScr() {
        preferenceHandler.writeBoolean(preferenceHandler.locationPermission, true)
        Intent(
            this,
            HomeActivity::class.java
        ).also {
            startActivity(it)
            finish()
        }
    }

    private fun storeLocation(){
        locationRequest = LocationRequest.create().apply {
            interval = 10000 // Update interval in milliseconds
            fastestInterval = 5000 // Fastest update interval in milliseconds
            priority = Priority.PRIORITY_HIGH_ACCURACY // You can change the priority based on your requirements
        }
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest!!,object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                userLocation=Location(location?.longitude, location?.latitude)
                viewModelUserLogin.getUser()?.userId?.let { userId ->
                    userLocation?.let { location ->
                        viewModel.updateUserLocation(userId, LocationReq(location = location))
                    }
                }
            }
        }, null)


    }

    private val locationPermissionStrings =
        arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)

    override fun onDestroy() {
        super.onDestroy()
        locationRequest = null
    }
}