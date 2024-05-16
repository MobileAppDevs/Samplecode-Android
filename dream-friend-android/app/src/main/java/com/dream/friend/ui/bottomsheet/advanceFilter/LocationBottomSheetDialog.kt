package com.dream.friend.ui.bottomsheet.advanceFilter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dream.friend.R
import com.dream.friend.common.Utils.dialogLoading
import com.dream.friend.common.Utils.openPermissionSettings
import com.dream.friend.common.Utils.permissionAlert
import com.dream.friend.common.Utils.showToast
import com.dream.friend.data.model.user.Location
import com.dream.friend.interfaces.LocationAdapterListener
import com.dream.friend.interfaces.LocationListener
import com.dream.friend.ui.location_permissions.LocationAdapter
import com.dream.friend.ui.viewModel.HomeScreenViewModel
import com.dream.friend.ui.viewModel.UserLoginViewModel
import com.dream.friend.util.AppUtil.getCity
import com.dream.friend.util.Extensions.visible
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener


class LocationBottomSheetDialog(var listener:LocationListener) :
    BottomSheetDialogFragment(), View.OnClickListener ,TextWatcher{
    var verifiedProfilesOnly: Boolean = false
    private val viewModelUserLogin: UserLoginViewModel by viewModels()
    private val viewModel: HomeScreenViewModel by viewModels()
    lateinit var loadingDialog: Dialog
    lateinit var locationAdapter: LocationAdapter
    lateinit var views: View
    var cityFinal: String? = null
    var lat: Double? = null
    var long: Double? = null
    lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenBottomSheetDialog)
    }


    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view1 = inflater.inflate(R.layout.bottomsheet_location, container, false)
        views = view1
        loadingDialog = requireActivity().dialogLoading()
        loadingDialog.dismiss()
        init()
        return view1
    }

    fun init() {
        views.findViewById<ImageView>(R.id.ivBack).setOnClickListener(this)
       val rv= views.findViewById<RecyclerView>(R.id.rvLocations)
        val displayMetrics = requireActivity().resources.displayMetrics
        val height = displayMetrics.heightPixels
        rv.minimumHeight=(height- views.findViewById<MaterialButton>(R.id.btnSetLocation).height)
        views.findViewById<EditText>(R.id.etSearch).addTextChangedListener(this)
        views.findViewById<MaterialButton>(R.id.btnSetLocation).setOnClickListener {
            dismiss()
            listener.onLocationSelected(lat, long, cityFinal)
        }
        views.findViewById<TextView>(R.id.tvCurrentLocation).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                /** Android 10 and above **/
                getCurrentLocationFromAndroid10andAbove()
            } else {
                /** below Android 10 **/
                getCurrentLocationFromAndroid7andBelowDevices()
            }
        }
        Places.initialize(requireActivity().applicationContext, getString(R.string.place_api_key));
        placesClient = Places.createClient(requireActivity())
        locationAdapter = LocationAdapter(placesClient, object : LocationAdapterListener {
            override fun onItemClicked(latt: Double, longg: Double, city: String) {
                views.findViewById<EditText>(R.id.etSearch).setText(city)
                cityFinal = city
                lat = latt
                long = longg
                dismiss()
                listener.onLocationSelected(lat,long,cityFinal)
            }

        })
        rv.adapter = locationAdapter
        rv.layoutManager = LinearLayoutManager(requireActivity())
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                dismiss()
            }

        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        getAutocomplete(mPlacesClient = placesClient, s.toString())
    }

    override fun afterTextChanged(s: Editable?) {

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
                requireActivity().showToast("something went wrong")
            }
        }
    }
    private fun openSettingLocation() {

        requireActivity().permissionAlert(
            message = "Location permission is Required.",
            title = "Permission",
            successBtnText = "Request",
            callback = { dialog: DialogInterface, i: Int ->
                if (i == DialogInterface.BUTTON_POSITIVE)
                    requireActivity().openPermissionSettings()
                else dialog.dismiss()
            }
        )
    }

    private fun popUpForTurningOnGPS() {
        turnGPSOn()
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
        val locationManager = requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager

        // Check if GPS is enabled
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS is disabled, so we need to enable it

            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocationFromAndroid7andBelowDevices() {
        Dexter.withContext(requireActivity())
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.RECORD_AUDIO
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    (requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager).requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0L,
                        0f,
                        object : android.location.LocationListener {
                            override fun onLocationChanged(location: android.location.Location) {
                              val  userLocation = Location(location.longitude, location.latitude)
                                lat= userLocation.lat
                                long= userLocation.long
                                cityFinal= lat?.let { long?.let { it1 -> requireActivity().getCity(it, it1) } }
                                listener.onLocationSelected(lat, long, cityFinal)
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


    }


    @SuppressLint("MissingPermission")
    private fun getCurrentLocationFromAndroid10andAbove() {
        Dexter.withContext(requireActivity())
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    LocationServices.getFusedLocationProviderClient(requireActivity()).lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            lat= location.latitude
                            long= location.longitude
                            cityFinal= lat?.let { long?.let { it1 -> requireActivity().getCity(it, it1) } }
                            dismiss()
                            listener.onLocationSelected(lat, long,cityFinal)
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
    fun showPermissionDeniedDialog() {
      requireActivity().  showToast(
            "Please give location Permission and try again. Since it is required to fetch your current location",
//            callback = { _: DialogInterface, i: Int ->
//                if (i == DialogInterface.BUTTON_POSITIVE) {
//                   views.findViewById<TextView>(R.id.tvCurrentLocation).performClick()
//                }
//            }
        )
    }

}