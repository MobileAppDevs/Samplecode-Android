package com.dream.friend.ui.settings

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dream.friend.R
import com.dream.friend.common.Utils.dialogLoading
import com.dream.friend.common.Utils.openPermissionSettings
import com.dream.friend.common.Utils.permissionAlert
import com.dream.friend.common.Utils.showToast
import com.dream.friend.common.activityBindings
import com.dream.friend.common.startRotationAnimation
import com.dream.friend.data.model.peronal_details.LocationReq
import com.dream.friend.data.model.user.Location
import com.dream.friend.data.network.Resource
import com.dream.friend.databinding.ActivityLocationSettingBinding
import com.dream.friend.interfaces.LocationAdapterListener
import com.dream.friend.ui.location_permissions.LocationAdapter
import com.dream.friend.ui.viewModel.UpdateUserDetailsViewModel
import com.dream.friend.ui.viewModel.UserLoginViewModel
import com.dream.friend.util.AppUtil.getCity
import com.dream.friend.util.Extensions.visible
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener


class LocationSettingActivity : AppCompatActivity(), TextWatcher {

    private val binding: ActivityLocationSettingBinding by activityBindings(R.layout.activity_location_setting)
//    private var locationManager: LocationManager? = null
//    private var userLocation: Location? = null
    private lateinit var dialog: Dialog
//    private lateinit var preferenceHandler: PreferenceHandler
    private val viewModelUserLogin: UserLoginViewModel by viewModels()
    private val viewModel: UpdateUserDetailsViewModel by viewModels()
   private lateinit var locationAdapter: LocationAdapter
    private lateinit var placesClient: PlacesClient
    var cityFinal: String? = null
    var lat: Double? = null
    var long: Double? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        dialog = dialogLoading()
        dialog.dismiss()
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.etSearch.addTextChangedListener(this)

        binding.tvCurrentLocation.setOnClickListener {
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                *//** Android 10 and above **//*
                getCurrentLocationFromAndroid10andAbove()
            } else {
                *//** below Android 10 **//*
                getCurrentLocationFromAndroid7andBelowDevices()
            }*/
            getCurrentLocationFromAndroid10andAbove()
        }
        binding.btnSetLocation.setOnClickListener {
            if (intent.getStringExtra("fromScreen") != null) {
                //from advance filter

            } else {
                viewModelUserLogin.getUser()?.userId?.let { userId ->
                    viewModel.updateUserLocation(
                        userId,
                        LocationReq(location = lat?.let { it1 ->
                            long?.let { it2 ->
                                Location(
                                    lat = it1,
                                    long = it2
                                )
                            }
                        }, city = cityFinal)
                    )
                    dialog.show()
                }
            }

        }

        Places.initialize(applicationContext, getString(R.string.place_api_key));
        placesClient = Places.createClient(this)
        binding.etSearch.addTextChangedListener(this)
        setAdapter(placesClient)
        locationResponse()
    }

    private fun setAdapter(placesClient: PlacesClient) {
        locationAdapter = LocationAdapter(placesClient, object : LocationAdapterListener {
            override fun onItemClicked(latt: Double, longg: Double, city: String) {
                binding.btnSetLocation.visible()
                binding.etSearch.setText(city)
                cityFinal = city
                lat = latt
                long = longg
            }

        })
        binding.rvLocations.adapter = locationAdapter
        binding.rvLocations.layoutManager = LinearLayoutManager(this)
    }

    private fun openSettingLocation() {

        permissionAlert(
            message = "Location permission is Required.",
            title = "Permission",
            successBtnText = "Request",
            callback = { dialog: DialogInterface, i: Int ->
                if (i == DialogInterface.BUTTON_POSITIVE)
                    openPermissionSettings()
                else dialog.dismiss()
            }
        )
    }

    private fun popUpForTurningOnGPS() {
//        successFailureAlert(
//            "Please enable your GPS.",
//            callback = { _: DialogInterface, i: Int ->
//                if (i == DialogInterface.BUTTON_POSITIVE) {
                    turnGPSOn()
//                }
//            }
//        )
    }

    private fun turnGPSOn() {
//        val provider: String =
//            Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
//        if (!provider.contains("gps")) { //if gps is disabled
//            val poke = Intent()
//            poke.setClassName(
//                "com.android.settings",
//                "com.android.settings.widget.SettingsAppWidgetProvider"
//            )
//            poke.addCategory(Intent.CATEGORY_ALTERNATIVE)
//            poke.data = Uri.parse("3")
//            sendBroadcast(poke)
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // Check if GPS is enabled
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS is disabled, so we need to enable it

            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

/*    @SuppressLint("MissingPermission")
    private fun getCurrentLocationFromAndroid7andBelowDevices() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.RECORD_AUDIO
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    (getSystemService(LOCATION_SERVICE) as LocationManager).requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0L,
                        0f,
                        object : LocationListener {
                            override fun onLocationChanged(location: android.location.Location) {
                                userLocation = Location(location.longitude, location.latitude)
                                lat= userLocation!!.lat
                                long= userLocation!!.long
                                cityFinal= lat?.let { long?.let { it1 -> getCity(it, it1) } }
                                viewModelUserLogin.getUser()?.userId?.let { userId ->
                                    viewModel.updateUserLocation(
                                        userId,
                                        LocationReq(location =
                                                Location(
                                                    lat = userLocation!!.lat,
                                                    long = userLocation!!.long
                                                ), city = cityFinal)
                                    )
                                    dialog.show()
                                }
                            }

                            override fun onProviderEnabled(provider: String) {
                                getCurrentLocationFromAndroid7andBelowDevices()
                            }

                            override fun onProviderDisabled(provider: String) {
                                popUpForTurningOnGPS()
                            }
                        }
                    )
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    openSettingLocation()
                }
            }).check()


    }*/


    @SuppressLint("MissingPermission")
    private fun getCurrentLocationFromAndroid10andAbove() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    LocationServices.getFusedLocationProviderClient(this@LocationSettingActivity).lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            lat= location.latitude
                            long= location.longitude
                            cityFinal= lat?.let { long?.let { it1 -> getCity(it, it1) } }
                            viewModelUserLogin.getUser()?.userId?.let { userId ->
                                viewModel.updateUserLocation(
                                    userId,
                                    LocationReq(location =
                                    Location(
                                        lat = lat,
                                        long = long
                                    ), city = cityFinal)
                                )
                                dialog.show()
                            }
                        }
                    }
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    showPermissionDeniedDialog()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    openSettingLocation()
                }
            }).check()

    }


    override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
        getAutocomplete(mPlacesClient = placesClient, s.toString())
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(p0: Editable?) {
    }

    private fun getAutocomplete(
        mPlacesClient: PlacesClient,
        constraint: CharSequence
    ) {
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(constraint.toString()).build()
        val prediction = mPlacesClient.findAutocompletePredictions(request)
        prediction.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                locationAdapter.setItems(task.result.autocompletePredictions)
                locationAdapter.notifyDataSetChanged()
            } else {
                showToast("something went wrong")
            }
        }
    }

    private fun locationResponse() {
        viewModel.updateUserLocationResponse.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let {
                        viewModelUserLogin.saveUser(it.user)
                        dialog.dismiss()
                        showToast("Setting Updated successfully")
                        val intent = Intent();
                        intent.putExtra("city", cityFinal)
                        intent.putExtra("lat", lat)
                        intent.putExtra("long", long)
                        setResult(RESULT_OK, intent);
                        finish()
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
                    dialog.findViewById<ImageView>(R.id.imgAnimation)
                        .startRotationAnimation()
                }

                is Resource.TokenRenew -> {
                    dialog.dismiss()
                }
            }
        }
    }

    fun showPermissionDeniedDialog() {
       showToast(
            "Please give location Permission. Since it is required to fetch your current location",
//            callback = { _: DialogInterface, i: Int ->
//                if (i == DialogInterface.BUTTON_POSITIVE) {
//                    binding.tvCurrentLocation.performClick()
//                }
//            }
        )
    }
}