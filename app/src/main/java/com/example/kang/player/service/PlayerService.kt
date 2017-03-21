package com.example.kang.player.service

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import com.example.kang.player.logic.MusicStateMachine
import com.example.kang.player.model.Music
import com.example.kang.player.model.PlayMode
import com.example.kang.player.model.PlayerState
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
    private val playlist: MutableList<Music> = arrayListOf()
    private var mode: PlayMode = PlayMode.ORDER
    private var index: Int = DEFAULT_MUSIC_INDEX

    private val clientMessgengerList: MutableList<Messenger> = arrayListOf()

    val player: ExoPlayer by lazy {
        // 1. Create a default TrackSelector
        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveVideoTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        // 2. Create a default LoadControl
        val loadControl = DefaultLoadControl()
        // 3. Create the player
        ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl)
    }
    val playerSm: MusicStateMachine by lazy {
        MusicStateMachine(this.javaClass.name, player)
    }

    private val messenger = Messenger(PlayerServiceHandler())

    inner class PlayerServiceHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_SWITCH -> {
                    switch(msg.obj as Uri)
                    play()
                }
                MSG_GET_INFO -> {
                    val temp = Message.obtain(null, MSG_GET_INFO)
                    temp.obj = player.playWhenReady
                    msg.replyTo.send(temp)
                    return
                }
                MSG_INIT_SERVICE -> useStateOrIgnore(msg)
                MSG_PAUSE -> pause()
                MSG_PLAY -> play()
                MSG_NEXT_SONG -> next()
                MSG_PREV_SONG -> previous()
                MSG_UN_SUBSCRIBE_CLIENT -> clientMessgengerList.remove(msg.replyTo)
                else -> super.handleMessage(msg)
            }
        }

        /**
         * accept state when [PlayerService.playlist] is empty
         */
        private fun useStateOrIgnore(msg: Message) {
            val state = msg.obj as PlayerState

            if (playlist.isNotEmpty()) {
                return
            }

            playlist.clear()
            playlist.addAll(state.playlist)
            mode = state.playMode
            index = state.index
        }
    }

    private fun next() {
        index = (index + 1) % playlist.size
        switch(playlist[index].uri)
        playerSm.songPlay()
    }

    private fun previous() {
        index = if (index <= 0) {
            playlist.size - 1
        } else {
            (index - 1) % playlist.size
        }

        switch(playlist[index].uri)
        playerSm.songPlay()
    }

    private fun pause() {
        playerSm.songPause()
    }

    private fun play() {
        if (index == DEFAULT_MUSIC_INDEX) {
            ++index
            switch(playlist[index].uri)
        }
        playerSm.songPlay()
    }

    private fun switch(uri: Uri) {
        val source = createSourceFromUri(uri)
        playerSm.songSwitch(source)
    }

    override fun onCreate() {
        super.onCreate()
        playerSm.start()
    }

    override fun onDestroy() {
        playerSm.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        return messenger.binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    private fun createSourceFromUri(uri: Uri): ExtractorMediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "yourApplicationName"), DefaultBandwidthMeter())
        // Produces Extractor instances for parsing the media data.
        val extractorsFactory = DefaultExtractorsFactory()
        val source = ExtractorMediaSource(uri,
                dataSourceFactory, extractorsFactory, null, null)
        return source
    }

    companion object {
        val DEFAULT_MUSIC_INDEX = -1

        val MSG_INIT_SERVICE = -1
        val MSG_PLAY = 0x00
        val MSG_PAUSE = 0x01
        val MSG_SWITCH = 0x02
        val MSG_GET_INFO = 0x03
        val MSG_NEXT_SONG = 0x04
        val MSG_PREV_SONG = 0x05
        val MSG_UN_SUBSCRIBE_CLIENT = 0x06

    }
}
