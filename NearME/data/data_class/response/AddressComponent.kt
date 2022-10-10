package com.ongraph.nearme.data.data_class.response

import com.google.gson.annotations.SerializedName

data class AddressComponent (
    @SerializedName("long_name"  ) var longName  : String?            = null,
    @SerializedName("short_name" ) var shortName : String?            = null,
    @SerializedName("types"      ) var types     : ArrayList<String>? = null
)