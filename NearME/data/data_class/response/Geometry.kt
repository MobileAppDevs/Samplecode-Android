package com.ongraph.nearme.data.data_class.response

import com.google.gson.annotations.SerializedName

data class Geometry (

  @SerializedName("location" ) var location : Location?,
  //@SerializedName("viewport" ) var viewport : Viewport?

)

data class Location (

  @SerializedName("lat" ) var lat : Double?,
  @SerializedName("lng" ) var lng : Double?

)

data class Viewport (

  @SerializedName("northeast" ) var northeast : Northeast?,
  @SerializedName("southwest" ) var southwest : Southwest?

)

data class Northeast (

  @SerializedName("lat" ) var lat : Double?,
  @SerializedName("lng" ) var lng : Double?

)

data class Southwest (

  @SerializedName("lat" ) var lat : Double?,
  @SerializedName("lng" ) var lng : Double?

)