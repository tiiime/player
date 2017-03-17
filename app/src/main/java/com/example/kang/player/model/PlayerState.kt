package com.example.kang.player.model

import java.util.*

/**
 * Created by kang on 17-3-17.
 */
data class PlayerState(
        var playlist: MutableCollection<Music>,
        var index: Int,
        var playMode: PlayMode,
        var playing: Boolean) : Cloneable {

    override fun clone(): PlayerState {
        val list = ArrayList<Music>()
        playlist.forEach {
            list.add(it.copy())
        }

        return PlayerState(list, index, playMode, playing)
    }
}

data class Music(val name: String, val duration: Long) : Cloneable {
    override fun clone(): Music {
        return Music(name, duration)
    }
}

enum class PlayMode {
    SHUFFLE,
    ORDER,
    LOOP
}