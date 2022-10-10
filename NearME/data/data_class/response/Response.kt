package com.ongraph.nearme.data.data_class.response

import com.google.gson.annotations.SerializedName

data class Response (

  @SerializedName("next_page_token"   ) var nextPageToken    : String?=null,
  @SerializedName("results"           ) var results          : List<Results> = listOf(),
  @SerializedName("result"            ) var result           : Results,
  @SerializedName("status"            ) var status           : String?

)