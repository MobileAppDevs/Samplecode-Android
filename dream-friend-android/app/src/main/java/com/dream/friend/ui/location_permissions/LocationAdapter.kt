package com.dream.friend.ui.location_permissions

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.dream.friend.R
import com.dream.friend.common.Utils.showToast
import com.dream.friend.data.model.CountryCode
import com.dream.friend.interfaces.CountryCodeItemSelectedListener
import com.dream.friend.interfaces.LocationAdapterListener
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient

class LocationAdapter(var mPlacesClient: PlacesClient,var listener:LocationAdapterListener) : RecyclerView.Adapter<LocationAdapter.MyViewHolder>() {

    private var myList = mutableListOf<AutocompletePrediction>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(list: MutableList<AutocompletePrediction>) {
        if (myList.isNotEmpty())
            myList.clear()
        myList.addAll(list)
        this.notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = myList[position]
        holder.tvPrimaryText.text = data.getPrimaryText(null).toString()
        holder.tvSecondaryText.text = data.getFullText(null).toString()

        holder.itemView.setOnClickListener {
            getLatLong(mPlacesClient,data.placeId,holder)
        }
    }

    override fun getItemCount(): Int = myList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =
        MyViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_location,
                    parent,
                    false
                )
        )

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPrimaryText: TextView = itemView.findViewById(R.id.tvPrimaryText)
        val tvSecondaryText: TextView = itemView.findViewById(R.id.tvSecondaryText)
    }
    private fun  getLatLong(mPlacesClient: PlacesClient, placeId:String, holder: MyViewHolder){
        val fields: List<Place.Field> = listOf(Place.Field.LAT_LNG)
        val fetchPlaceRequest = FetchPlaceRequest.builder(placeId, fields).build()
        mPlacesClient.fetchPlace(fetchPlaceRequest)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
                val latLng = place.latLng
                if (latLng != null) {
                    listener.onItemClicked(latLng.latitude,latLng.longitude,holder.tvPrimaryText.text.toString())
                }
            }
            .addOnFailureListener { exception: Exception ->
                holder.itemView.context.showToast(exception.localizedMessage)
            }
    }
}