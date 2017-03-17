package com.example.kang.player.logic.state

import android.os.Message
import com.example.kang.player.util.State
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer

/**
 * Created by kang on 17-3-16.
 */
class PauseState(val player: ExoPlayer) : State() {
    override fun enter() {
        super.enter()
        player.playWhenReady = false
    }

    override fun processMessage(msg: Message?): Boolean {
        return super.processMessage(msg)
    }

    override fun exit() {
        super.exit()
        player.playWhenReady = true
    }
}
