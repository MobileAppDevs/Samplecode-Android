package com.nisha.mvvmstructure.data.model

/**
 * demo data classes
 * */
data class ListItem(
    val id: String,
    val listItem1: String,
)

data class Response(
    val results: List<ListItem>
)