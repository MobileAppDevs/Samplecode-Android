package com.enkefalostechnologies.calendarpro.api.model

data class Item(
    val created: String,
    val creator: Creator,
    val description: String,
    val end: End,
    val etag: String,
    val eventType: String,
    val htmlLink: String,
    val iCalUID: String,
    val id: String,
    val kind: String,
    val organizer: Organizer,
    val sequence: Int,
    val start: Start,
    val status: String,
    val summary: String,
    val transparency: String,
    val updated: String,
    val visibility: String
)