package com.example.kang.player.model

import android.net.Uri
import java.util.*

/**
 * Created by kang on 17-3-17.
 */
data class PlayerState (
        var playlist: MutableList<Music>,
        var index: Int,
        var playMode: PlayMode,
        var playing: Boolean,
        var targetMusic: Music?) : Cloneable {

    override public fun clone(): PlayerState {
        val list = ArrayList<Music>()
        playlist.forEach {
            list.add(it.copy())
        }

        return PlayerState(list, index, playMode, playing, null)
    }
}

data class Music(val name: String, val duration: Long, val uri: Uri) : Cloneable {
    override fun clone(): Music {
        return Music(name, duration, uri)
    }
}

enum class PlayMode {
    SHUFFLE,
    ORDER,
    LOOP
}