package com.example.kang.player.logic.state

import android.os.Message
import com.example.kang.player.logic.MusicStateMachine
import com.example.kang.player.util.IState
import com.example.kang.player.util.State
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.MediaSource

/**
 * Created by kang on 17-3-16.
 */
class SwitchSongState(val player: ExoPlayer) : State() {
    override fun processMessage(msg: Message) = when (msg.what) {
        MusicStateMachine.CMD_SWITCH -> {
            player.prepare(msg.obj as MediaSource?)
            IState.HANDLED
        }
        else -> super.processMessage(msg)
    }
}
