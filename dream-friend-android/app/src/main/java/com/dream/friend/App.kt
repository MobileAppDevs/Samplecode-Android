package com.dream.friend

import android.app.Application
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.appcheck.ktx.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {

    private var instance: Application = this

    fun getContext(): Context {
        return instance
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )

        mediaPlayer = MediaPlayer()
        flag = true
    }

    companion object{
        private var mediaPlayer : MediaPlayer?=null
        var flag = false
        var IN_COMING_CALL = false
        var userCallDeclineObserver = MutableLiveData(false)

        fun stop() {
            if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                mediaPlayer?.stop()
                mediaPlayer?.release()
                this.mediaPlayer = null
            }
        }

        fun Context.set(ringId: Int? = null) {
            val defaultRingtoneUri: Uri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE)
            mediaPlayer = if (ringId == null)
                MediaPlayer.create(this, defaultRingtoneUri)
            else
                MediaPlayer.create(this, ringId)
        }

        fun play() {
            mediaPlayer?.apply {
                if (this.isPlaying) {
                    this.pause()
                    this.stop()
                }
                isLooping = true
                start()
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
            }
        }
    }
}