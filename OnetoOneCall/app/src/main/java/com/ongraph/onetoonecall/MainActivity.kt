package com.ongraph.onetoonecall

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.ongraph.onetoonecall.common.Constants.APP_ID
import com.ongraph.onetoonecall.data.data_class.Token
import com.ongraph.onetoonecall.data.networks.ApiClient
import com.ongraph.onetoonecall.databinding.ActivityMainBinding
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Main Activity for handling Agora RTC functionality.
 */
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var agoraEngine: RtcEngine? = null
    private val channelName = "" // Channel to join for one-to-one call
    private val userId = 0 // Your user ID

    /*** Initializes the activity and sets up UI elements.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        initSetup()
    }

    /**
     * Sets up click listeners for buttons and initializes Agora functionality.
     */
    private fun initSetup() {
        binding.btStartCall.setOnClickListener {
            val text = "Channel name: $channelName\nUser Id: $userId"
            binding.tvChannelUserData.text =text
            getToken(channelName, userId)
        }

        binding.btEndCall.setOnClickListener {
            agoraEngine?.leaveChannel()
            binding.btStartCall.isVisible = true
            binding.btEndCall.isVisible = false
        }
    }

    /**
     * Fetches an Agora token from the server.
     */
    private fun getToken(channelName: String, userId: Int) {
        ApiClient.getRtcService().getAgoraToken(channelName, userId).enqueue(object :
            Callback<Any?> {
            override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                setupVoiceSDKEngine((response.body() as Token?)?.rtcToken, channelName, userId)
            }

            override fun onFailure(call: Call<Any?>, t: Throwable) {}
        })
    }

    /**
     * Sets up the Agora RTC engine and joins the channel.
     */
    private fun setupVoiceSDKEngine(token: String?, channelName: String, userId: Int) {
        try {
            val config = RtcEngineConfig()
            config.mContext = this
            config.mAppId = APP_ID
            var str: String
            config.mEventHandler = object : IRtcEngineEventHandler() {
                override fun onUserJoined(uid: Int, elapsed: Int) {
                    super.onUserJoined(uid, elapsed)
                    str = "In Call"
                    binding.tvInCallStatus.text = str
                    binding.btEndCall.isVisible = true
                    binding.btStartCall.isVisible = false
                    // Remote user joined
                }

                override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                    super.onJoinChannelSuccess(channel, uid, elapsed)
                    str = "Ringing"
                    binding.tvInCallStatus.text = str
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Destroys the Agora RTC engine when the activity is destroyed.
     */
    override fun onDestroy() {
        super.onDestroy()
        RtcEngine.destroy()
    }
}