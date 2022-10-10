package com.ongraph.nearme.data.data_class.response

import com.google.gson.annotations.SerializedName

data class Results (
  @SerializedName("formatted_address"         ) var formattedAddress    : String?,
  @SerializedName("geometry"                  ) var geometry            : Geometry?,
  @SerializedName("name"                      ) var name                : String?,
  @SerializedName("opening_hours"             ) var openingHours        : OpeningHours?,
  @SerializedName("photos"                    ) var photos              : ArrayList<Photos>?,
  @SerializedName("place_id"                  ) var placeId             : String?,
  @SerializedName("rating"                    ) var rating              : Double?,
  @SerializedName("url"                       ) var url                 : String?,
  @SerializedName("price_level"               ) var price_level         : Int?,
  @SerializedName("vicinity"                  ) var vicinity            : String?,
  @SerializedName("types"                     ) var types               : List<String>,
  @SerializedName("user_ratings_total"        ) var userRatingsTotal    : Int?
)