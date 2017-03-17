package com.example.kang.player.model

/**
 * Created by kang on 17-3-17.
 */
data class PlayerState(
        var index: Int,
        var playMode: PlayMode
)

enum class PlayMode {
    SHUFFLE,
    ORDER,
    LOOP
}