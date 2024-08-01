package com.ongraph.onetoonecall.data.networks

import com.ongraph.onetoonecall.common.Constants.APP_ID
import com.ongraph.onetoonecall.common.Constants.CERTIFICATES
import retrofit2.Call
import retrofit2.http.*

/**
 * Service interface for fetching RTC tokens from the server.
 */
interface RtcTokenService {

    /**
     * Fetches an Agora token for the given channel and user ID.
     *
     * @param channelName The name of the channel.
     * @param userId The ID of the user.
     * @return A Call object representing the network request.
     */
    @GET("/rtc/${APP_ID}/${CERTIFICATES}/{channelName}/publisher/uid/{userId}")
    fun getAgoraToken(
        @Path("channelName") channelName: String,
        @Path("userId") userId:Int): Call<Any?>
}