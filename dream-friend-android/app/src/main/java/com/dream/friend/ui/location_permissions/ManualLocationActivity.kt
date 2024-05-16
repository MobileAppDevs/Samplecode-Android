package com.dream.friend.ui.location_permissions

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dream.friend.R
import com.dream.friend.common.PreferenceHandler
import com.dream.friend.common.Utils.dialogLoading
import com.dream.friend.common.Utils.showToast
import com.dream.friend.common.activityBindings
import com.dream.friend.common.startRotationAnimation
import com.dream.friend.data.model.peronal_details.LocationReq
import com.dream.friend.data.model.user.Location
import com.dream.friend.data.network.Resource
import com.dream.friend.databinding.ActivityHomeBinding
import com.dream.friend.databinding.ActivityManualLocationBinding
import com.dream.friend.interfaces.LocationAdapterListener
import com.dream.friend.ui.add_photo.AddPhotoActivity
import com.dream.friend.ui.home.HomeActivity
import com.dream.friend.ui.viewModel.UpdateUserDetailsViewModel
import com.dream.friend.ui.viewModel.UserLoginViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ManualLocationActivity : AppCompatActivity(), TextWatcher {
    private lateinit var dialog: Dialog
    private lateinit var preferenceHandler: PreferenceHandler
    private val viewModelUserLogin: UserLoginViewModel by viewModels()
    private val viewModel: UpdateUserDetailsViewModel by viewModels()
    lateinit var adapter: LocationAdapter
    lateinit var placesClient: PlacesClient
    private val binding: ActivityManualLocationBinding by activityBindings(R.layout.activity_manual_location)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        dialog = dialogLoading()
        dialog.dismiss()
        binding.toolbar.ivBack.setOnClickListener{finish()}
        binding.toolbar.tvBack.setOnClickListener{finish()}
        preferenceHandler = PreferenceHandler(this)
        Places.initialize(applicationContext, getString(R.string.place_api_key));
        placesClient = Places.createClient(this)
        binding.etText.addTextChangedListener(this)
        adapter = LocationAdapter(placesClient, object : LocationAdapterListener {
            override fun onItemClicked(lat: Double, long: Double,city:String) {
                viewModelUserLogin.getUser()?.userId?.let { userId ->
                        viewModel.updateUserLocation(userId, LocationReq(location = Location(lat=lat,long=long), city = city))
                        dialog.show()
                }
            }

        })
        binding.rvLocations.adapter = adapter
        binding.rvLocations.layoutManager = LinearLayoutManager(this);
        locationResponse()
    }

    private fun getAutocomplete(mPlacesClient: PlacesClient, constraint: CharSequence) {
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(constraint.toString()).build()
        val prediction = mPlacesClient.findAutocompletePredictions(request)
        prediction.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                adapter.setItems(task.result.autocompletePredictions)
                adapter.notifyDataSetChanged()
            } else {
                showToast("something went wrong")
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

    private fun locationResponse() {
        viewModel.updateUserLocationResponse.observe(this) { response->
            when(response) {
                is Resource.Success -> {
                    response.data?.let {
                        dialog.dismiss()
//                        successFailureAlert(
//                            it.message.toString(),
//                            "Success",
//                            callback = { _: DialogInterface, i: Int ->
//                                if (i == DialogInterface.BUTTON_POSITIVE)
                                    moveToNextScr(true)
//                            }
//                        )
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
    private fun moveToNextScr(isTrue: Boolean) {
        preferenceHandler.writeBoolean(preferenceHandler.locationPermission, isTrue)
        Intent(
            this,
            HomeActivity::class.java
        ).also {
            startActivity(it)
            finish()
        }
    }
}