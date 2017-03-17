package com.example.kang.player.model

/**
 * Created by kang on 17-3-17.
 */
data class PlayerState(
        var playlist: MutableCollection<Music>,
        var index: Int,
        var playMode: PlayMode
)

data class Music(val name: String, val duration: Long)

enum class PlayMode {
    SHUFFLE,
    ORDER,
    LOOP
}