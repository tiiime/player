package com.example.kang.player.model

import android.graphics.Bitmap
import android.net.Uri
import java.util.*

/**
 * Created by kang on 17-3-17.
 */
data class PlayerState (
        var playlist: MutableList<Music>,
        var index: Int,
        var playMode: PlayMode,
        var playingState: PlayingState,
        var currentMusic: Music?,
        var targetMusic: Music?) : Cloneable {

    override public fun clone(): PlayerState {
        val list = ArrayList<Music>()
        playlist.forEach {
            list.add(it.copy())
        }

        return PlayerState(list, index, playMode, playingState, currentMusic, null)
    }
}

data class Music(val name: String, val duration: Long, val uri: Uri, var album: Bitmap?) : Cloneable {
    override fun clone(): Music {
        return Music(name, duration, uri, null)
    }
}

enum class PlayMode {
    SHUFFLE,
    ORDER,
    LOOP
}