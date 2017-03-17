package com.example.kang.player.logic

import android.net.Uri
import android.os.Message
import com.example.kang.player.util.IState
import com.example.kang.player.util.State
import com.example.kang.player.util.StateMachine
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.MediaSource


/**
 * Created by kang on 17-3-16.
 *
 */
class MusicStateMachine(name: String, val player: ExoPlayer) : StateMachine(name) {

    companion object {
        internal val CMD_INIT = 0x00
        internal val CMD_PLAY = 0x01
        internal val CMD_SEEK = 0x02
        internal val CMD_RESET = 0x03
        internal val CMD_PAUSE = 0x04
        internal val CMD_BUFFER = 0x05
        internal val CMD_SWITCH = 0x06
    }

    private val initState = InitState()
    private val seekState = SeekState()
    private val pauseState = PauseState()
    private val resetState = ResetInfoState()
    private val bufferState = BufferState()
    private val switchState = SwitchSongState()
    private val playingState = PlayingState()

    /**
     *
     *                      +---------+
     *                      |  INIT   |
     *                      +---------+
     *                      /         \
     *           +----------+         +---------+
     *           |  BUFFER  |         |  PAUSE  |
     *           +----------+         +---------+
     *                |                  /   \
     *           +----------+  +---------+   +---------+
     *           |   PLAY   |  |  RESET  |   |   SEEK  |
     *           +----------+  +---------+   +---------+
     *                              |
     *                         +---------+
     *                         |  SWITCH |
     *                         +---------+
     */
    init {
        addState(initState, null)
            addState(bufferState, initState)
                addState(playingState, bufferState)
            addState(pauseState, initState)
                addState(seekState, pauseState)
                addState(resetState, pauseState)
                    addState(switchState, resetState)

        setInitialState(pauseState)
    }

    fun switch(uri: Uri) {
        val msg = obtainMessage(CMD_SWITCH)
        msg.obj = uri
        sendMessage(msg)
    }

    internal inner class InitState() : State() {

        override fun processMessage(msg: Message) = when (msg.what) {
            CMD_SWITCH -> {
                deferMessage(msg)
                transitionTo(switchState)
                IState.HANDLED
            }
            CMD_SEEK -> {
                deferMessage(msg)
                transitionTo(seekState)
                IState.HANDLED
            }
            else -> super.processMessage(msg)
        }

        override fun exit() {
            super.exit()
            player.release()
        }
    }

    internal inner class BufferState() : State() {
        override fun enter() {
            super.enter()
            player.playWhenReady = true
        }
    }

    internal inner class PauseState() : State() {
        override fun enter() {
            super.enter()
            player.playWhenReady = false
        }

        override fun processMessage(msg: Message?): Boolean {
            return super.processMessage(msg)
        }
    }

    internal inner class PlayingState() : State()

    internal inner class ResetInfoState() : State() {
        override fun enter() {
            super.enter()
            player.stop()
        }
    }

    internal inner class SeekState() : State() {
        override fun processMessage(msg: Message) = when (msg.what) {
            CMD_SEEK -> {
                player.seekTo(msg.obj as Long)
                IState.HANDLED
            }
            else -> super.processMessage(msg)
        }
    }

    internal inner class SwitchSongState() : State() {
        override fun processMessage(msg: Message) = when (msg.what) {
            CMD_SWITCH -> {
                player.prepare(msg.obj as MediaSource?)
                transitionTo(bufferState)
                IState.HANDLED
            }
            else -> super.processMessage(msg)
        }
    }
}