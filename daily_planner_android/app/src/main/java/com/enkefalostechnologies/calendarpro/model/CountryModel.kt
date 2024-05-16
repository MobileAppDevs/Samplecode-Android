package com.enkefalostechnologies.calendarpro.model

data class CountryModel(
    var name:String,
    var flag:String,
    var apiTag:String,
    var countryCode:String,
    var isChecked:Boolean?=false,
)
