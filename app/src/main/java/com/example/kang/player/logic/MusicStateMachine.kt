package com.example.kang.player.logic

import android.os.Message
import com.example.statemachien.StateMachine
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.MediaSource


/**
 * Created by kang on 17-3-16.
 *
 */
class MusicStateMachine(name: String, val player: ExoPlayer) : StateMachine(name) {

    companion object {
        internal val CMD_STOP_MACHINE = -1
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

    fun songPlay() {
        sendMessage(CMD_PLAY)
    }

    fun songPause() {
        sendMessage(CMD_PAUSE)
    }

    fun songSwitch(mediaSource: MediaSource) {
        val msg = obtainMessage(CMD_SWITCH)
        msg.obj = mediaSource
        sendMessage(msg)
    }

    fun stop() {
        sendMessage(CMD_STOP_MACHINE)
    }

    internal inner class InitState : com.example.statemachien.State() {

        override fun processMessage(msg: Message) = when (msg.what) {
            CMD_STOP_MACHINE -> {
                transitionToHaltingState()
                com.example.statemachien.IState.HANDLED
            }
            CMD_SWITCH -> {
                deferMessage(msg)
                transitionTo(switchState)
                com.example.statemachien.IState.HANDLED
            }
            CMD_SEEK -> {
                deferMessage(msg)
                transitionTo(seekState)
                com.example.statemachien.IState.HANDLED
            }
            CMD_PLAY -> {
                transitionTo(playingState)
                com.example.statemachien.IState.HANDLED
            }
            CMD_PAUSE -> {
                transitionTo(pauseState)
                com.example.statemachien.IState.HANDLED
            }
            else -> super.processMessage(msg)
        }

        override fun exit() {
            super.exit()
            player.release()
        }
    }

    internal inner class BufferState : com.example.statemachien.State() {
        override fun enter() {
            super.enter()
            player.playWhenReady = true
        }
    }

    internal inner class PauseState : com.example.statemachien.State(){
        override fun enter() {
            super.enter()
            player.playWhenReady = false
        }

        override fun processMessage(msg: Message?): Boolean {
            return super.processMessage(msg)
        }
    }

    internal inner class PlayingState : com.example.statemachien.State() {
        override fun enter() {
            super.enter()
        }

        override fun processMessage(msg: Message) = when (msg.what) {
            else -> super.processMessage(msg)
        }
    }

    internal inner class ResetInfoState : com.example.statemachien.State() {
        override fun enter() {
            super.enter()
        }
    }

    internal inner class SeekState : com.example.statemachien.State() {
        override fun processMessage(msg: Message) = when (msg.what) {
            CMD_SEEK -> {
                player.seekTo(msg.obj as Long)
                com.example.statemachien.IState.HANDLED
            }
            else -> super.processMessage(msg)
        }
    }

    internal inner class SwitchSongState : com.example.statemachien.State() {
        override fun processMessage(msg: Message) = when (msg.what) {
            CMD_SWITCH -> {
                player.prepare(msg.obj as MediaSource?)
                com.example.statemachien.IState.HANDLED
            }
            else -> super.processMessage(msg)
        }
    }
}