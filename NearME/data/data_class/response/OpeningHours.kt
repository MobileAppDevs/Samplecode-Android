package com.ongraph.nearme.data.data_class.response

import com.google.gson.annotations.SerializedName

data class OpeningHours (

  @SerializedName("open_now" )    var openNow     : Boolean?,
  @SerializedName("periods"  )    var periods     : ArrayList<OpeningHoursPeriod>?,
  @SerializedName("weekday_text") var weekdayText : ArrayList<String>?

)

data class OpeningHoursPeriod(
  @SerializedName("close") var close: OpeningHoursPeriodDetails,
  @SerializedName("open" ) var open : OpeningHoursPeriodDetails
)

data class OpeningHoursPeriodDetails(
  @SerializedName("day" ) var day : Int,
  @SerializedName("time") var time: String
)