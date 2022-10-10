package com.ongraph.androisample.data.networks

import com.ongraph.androisample.common.Constants.appId
import com.ongraph.androisample.common.Constants.certificate
import retrofit2.Call
import retrofit2.http.*

interface RtcTokenService {

    @GET("/rtc/${appId}/${certificate}/{channelName}/publisher/uid/{userId}")
    fun getAgoraToken(
        @Path("channelName") channelName: String,
        @Path("userId") userId:Int): Call<Any?>
}