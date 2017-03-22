package com.example.kang.player.model

/**
 * Created by kang on 17-3-22.
 */
data class PlayingState(
        val duration: Long,
        val currentPosition: Long,
        val bufferPosition: Long,
        val playing: Boolean
)
