package com.ongraph.androisample.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.ongraph.androisample.R
import com.ongraph.androisample.common.Constants.appId
import com.ongraph.androisample.data.data_class.Token
import com.ongraph.androisample.data.networks.ApiClient
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import kotlinx.android.synthetic.main.activity_one_to_one_call.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OneToOneCallActivity : AppCompatActivity() {

    var agoraEngine: RtcEngine? = null
    private val channelName = ""
    private val userId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_to_one_call)

        startCall.setOnClickListener {
            getToken(channelName, userId)
        }

        endCall.setOnClickListener {
            agoraEngine?.leaveChannel()
            startCall.isVisible = true
            endCall.isVisible = false
        }
    }

    private fun getToken(channelName: String, userId: Int) {
        ApiClient.getRtcService().getAgoraToken(channelName, userId).enqueue(object : Callback<Any?> {
            override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                setupVoiceSDKEngine((response.body() as Token?)?.rtcToken, channelName, userId)
            }

            override fun onFailure(call: Call<Any?>, t: Throwable) {}
        })
    }

    private fun setupVoiceSDKEngine(token: String?, channelName: String, userId: Int) {
        try {
            val config = RtcEngineConfig()
            config.mContext = this
            config.mAppId = appId
            config.mEventHandler = object : IRtcEngineEventHandler() {
                override fun onUserJoined(uid: Int, elapsed: Int) {
                    super.onUserJoined(uid, elapsed)
                    inCall.text = "In Call"
                    endCall.isVisible = true
                    startCall.isVisible = false
                    //Remote user joined
                }
                override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                    super.onJoinChannelSuccess(channel, uid, elapsed)
                    inCall.text = "Ringing"
                    // Successfully joined a channel
                    // Waiting for a remote user to join
                }
                override fun onUserOffline(uid: Int, reason: Int) {
                    super.onUserOffline(uid, reason)
                    // Listen for remote users leaving the channel
                    // Remote user offline
                    // Waiting for a remote user to join
                }
                override fun onLeaveChannel(stats: RtcStats?) {
                    super.onLeaveChannel(stats)
                    // Listen for the local user leaving the channel
                    // Press the button to join a channel
                }
            }
            agoraEngine = RtcEngine.create(config)
            agoraEngine?.joinChannel(token, channelName, "", userId)
        }catch (e: Exception) { e.printStackTrace() }
    }

    override fun onDestroy() {
        super.onDestroy()
        RtcEngine.destroy()
    }
}