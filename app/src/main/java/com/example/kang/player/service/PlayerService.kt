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
import com.example.kang.player.model.PlayingState
import com.example.kang.player.util.*
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource.FLAG_BLOCK_ON_CACHE
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache


class PlayerService : Service(), ExoEventListener {
    private val playlist: MutableList<Music> = arrayListOf()
    private var mode: PlayMode = PlayMode.ORDER
    private var index: Int = DEFAULT_MUSIC_INDEX

    private val clientMessengerList: MutableList<Messenger> = arrayListOf()

    val player: SimpleExoPlayer by lazy {
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
                MSG_GET_PLAYING_INFO -> {
                    val temp = Message.obtain(null, MSG_GET_PLAYING_INFO)
                    temp.obj = PlayingState(
                            duration = player.duration,
                            currentPosition = player.currentPosition,
                            bufferPosition = player.bufferedPosition,
                            playing = player.playWhenReady
                    )
                    msg.replyTo.send(temp)
                    return
                }
                MSG_SWITCH -> {
                    switch(msg.obj as Uri)
                    play()
                }
                MSG_SEEK -> {
                    seek(msg.obj as Long)
                    play()
                }
                MSG_INIT_SERVICE -> useStateOrIgnore(msg)
                MSG_PAUSE -> pause()
                MSG_PLAY -> play()
                MSG_NEXT_SONG -> next()
                MSG_PREV_SONG -> previous()
                MSG_UN_SUBSCRIBE_CLIENT -> clientMessengerList.remove(msg.replyTo)
                else -> super.handleMessage(msg)
            }
        }

        /**
         * accept state when [PlayerService.playlist] is empty
         */
        private fun useStateOrIgnore(msg: Message) {
            clientMessengerList.add(msg.replyTo)
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

    private fun seek(position: Long) {
        playerSm.songSeek(position)
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

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
        trackGroups?.let {
            (0..trackGroups.length - 1).forEach { i ->
                val trackGroup = trackGroups.get(i)
                (0..trackGroup.length - 1).forEach { j ->
                    val metadata = trackGroup.getFormat(j).metadata
                    metadata?.let {
                        notifyClient(MSG_SWITCH, Music(
                                "${metadata.getSongName()}-${metadata.getArtistName()}",
                                0,
                                Uri.EMPTY,
                                metadata.getArtwork()
                        ))
                    }
                }
            }
        }
    }

    override fun onPositionDiscontinuity() {
        super.onPositionDiscontinuity()
        updateProgressBar()
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        updateProgressBar()
    }


    override fun onLoadingChanged(isLoading: Boolean) {
        super.onLoadingChanged(isLoading)
        updateProgressBar()
    }

    private fun updateProgressBar() {
        notifyClient(MSG_GET_PLAYING_INFO, PlayingState(
                duration = player.duration,
                currentPosition = player.currentPosition,
                bufferPosition = player.bufferedPosition,
                playing = player.playWhenReady
        ))
    }

    private fun notifyClient(caseAction: Int, obj: Any) {
        clientMessengerList.forEach {
            val msg = Message.obtain(null, caseAction)
            msg.obj = obj
            it.send(msg)
        }
    }

    override fun onCreate() {
        super.onCreate()
        playerSm.start()
        player.addListener(this)
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

    private fun createSourceFromUri(uri: Uri): MediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(this, "Player", DefaultBandwidthMeter())

        val cache = SimpleCache(cacheDir, LeastRecentlyUsedCacheEvictor(1024 * 1024 * 30))
        val cacheDataSourceFactory = CacheDataSourceFactory(cache, dataSourceFactory, FLAG_BLOCK_ON_CACHE)

        val mp3ExtraFactory = Mp3Extractor.FACTORY
        val source = ExtractorMediaSource(uri, cacheDataSourceFactory, mp3ExtraFactory, null, null)

        return source
    }

    companion object {
        val DEFAULT_MUSIC_INDEX = -1

        val MSG_INIT_SERVICE = -1
        val MSG_PLAY = 0x00
        val MSG_PAUSE = 0x01
        val MSG_SWITCH = 0x02
        val MSG_GET_PLAYING_INFO = 0x03
        val MSG_NEXT_SONG = 0x04
        val MSG_PREV_SONG = 0x05
        val MSG_UN_SUBSCRIBE_CLIENT = 0x06
        val MSG_SEEK = 0x07
    }
}
