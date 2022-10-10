package com.ongraph.nearme

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.ongraph.nearme.data.data_class.response.Response
import com.ongraph.nearme.data.data_class.response.Results
import com.ongraph.nearme.data.network.NearByMeRetrofit
import com.ongraph.nearme.data.network.Service
import com.ongraph.nearme.data.repository.ApiRepository

class AroundMeActivity : AppCompatActivity(), com.ongraph.nearme.data.repository.Result,
    OnMapReadyCallback {
    private lateinit var apiRepository: ApiRepository
    private lateinit var service: Service
    private var type = "places"
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arround_meactivity)

        service = NearByMeRetrofit().getInstance()

        apiRepository = ApiRepository(service, this)

        setMapView()
    }

    private fun setMapView() {
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        val latLng = LatLng(32.0, 74.92)
        mMap.addMarker(MarkerOptions().position(latLng).title(null))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        getPlaces()
    }

    private fun getPlaces () {
        type = "places"
        apiRepository.getPlaces("Places in India")
    }

    override fun onFailure(t: Throwable) {
        Toast.makeText(this, "${t.message}", Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess(data: Any?) {
        if (data != null) {
            if (type == "places") {
                val result =  (data as Response).results
                addMarkers(result)
            }
        }
    }

    private fun addMarkers(data: List<Results>?) {
        var marker: MarkerOptions
        var ic: Int

        mMap.clear()

        if (data != null) {
            for (d in data) {
                ic = R.drawable.marker

                marker = MarkerOptions().position(
                    LatLng(
                        d.geometry?.location?.lat!!,
                        d.geometry?.location?.lng!!
                    )
                )
                    .title(Gson().toJson(d))
                marker.icon(
                    BitmapDescriptorFactory.fromBitmap(
                        Bitmap.createScaledBitmap(
                            (ResourcesCompat.getDrawable(resources, ic, null) as BitmapDrawable)
                                .bitmap, 150, 150, false
                        )
                    )
                )
                mMap.addMarker(
                    marker
                        .position(
                            LatLng(d.geometry?.location?.lat!!, d.geometry?.location?.lng!!)
                        )
                        .title(
                            d.name
                        )
                )
            }
        }
        val latLng = LatLng(data?.get(0)?.geometry?.location?.lat!!, data[0].geometry?.location?.lng!!)
        mMap.addMarker(MarkerOptions().position(latLng).title(null))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    override fun onSuccessDistance(data: Any?) {
//        TODO("Not yet implemented")
    }

    override fun onSuccessWeather(data: Any?) {
//        TODO("Not yet implemented")
    }
}