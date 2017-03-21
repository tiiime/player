package com.example.kang.player.service

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
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

    private val playerBinder = PlayerBinder()

    fun pause() {
        playerSm.pause()
    }

    fun play() {
        playerSm.play()
    }

    fun switch(uri: Uri) {
        val source = createSourceFromUri(uri)
        playerSm.switch(source)
        playerSm.play()
    }

    override fun onBind(intent: Intent): IBinder {
        playerSm.start()
        return playerBinder
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

    inner class PlayerBinder : Binder() {
        fun getService(): PlayerService {
            return this@PlayerService
        }
    }
}
