package com.example.kang.player.service

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import com.example.kang.player.logic.MusicStateMachine
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util


class PlayerService : Service() {
    // 1. Create a default TrackSelector
    val bandwidthMeter = DefaultBandwidthMeter()
    val videoTrackSelectionFactory = AdaptiveVideoTrackSelection.Factory(bandwidthMeter)
    val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
    // 2. Create a default LoadControl
    val loadControl = DefaultLoadControl()
    // 3. Create the player
    val player: ExoPlayer by lazy {
        ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl)
    }
    val playerSm: MusicStateMachine by lazy {
        MusicStateMachine(this.javaClass.name, player)
    }

    private val messenger = Messenger(PlayerServiceHandler())

    inner class PlayerServiceHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_PAUSE -> {
                    pause()
                }
                MSG_PLAY -> {
                    play()
                }
                MSG_SWITCH -> {
                    switch(msg.obj as Uri)
                }
                MSG_GET_INFO->{
                    val temp = Message.obtain(null,MSG_GET_INFO)
                    temp.obj = player.playWhenReady
                    msg.replyTo.send(temp)
                    return
                }
                else -> {
                    super.handleMessage(msg)
                }
            }
        }
    }

    private fun pause() {
        playerSm.songPause()
    }

    private fun play() {
        playerSm.songPlay()
    }

    private fun switch(uri: Uri) {
        val source = createSourceFromUri(uri)
        playerSm.songSwitch(source)
        playerSm.songPlay()
    }

    override fun onCreate() {
        super.onCreate()
        playerSm.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerSm.songPause()
        playerSm.stop()
    }

    override fun onBind(intent: Intent): IBinder {
        return messenger.binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    private fun createSourceFromUri(uri: Uri): ExtractorMediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "yourApplicationName"), bandwidthMeter)
        // Produces Extractor instances for parsing the media data.
        val extractorsFactory = DefaultExtractorsFactory()
        val source = ExtractorMediaSource(uri,
                dataSourceFactory, extractorsFactory, null, null)
        return source
    }

    companion object {
        val MSG_PLAY = 0x00
        val MSG_PAUSE = 0x01
        val MSG_SWITCH = 0x02
        val MSG_GET_INFO = 0x03
    }
}
