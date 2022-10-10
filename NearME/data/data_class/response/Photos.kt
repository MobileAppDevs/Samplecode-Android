package com.ongraph.nearme.data.data_class.response

import com.google.gson.annotations.SerializedName


data class Photos (

  @SerializedName("height"            ) var height           : Int?,
  @SerializedName("html_attributions" ) var htmlAttributions : ArrayList<String> = arrayListOf(),
  @SerializedName("photo_reference"   ) var photoReference   : String?,
  @SerializedName("width"             ) var width            : Int?

)