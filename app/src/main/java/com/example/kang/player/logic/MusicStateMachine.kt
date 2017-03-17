package com.example.kang.player.logic

import com.example.kang.player.logic.state.*
import com.example.kang.player.util.StateMachine
import com.google.android.exoplayer2.ExoPlayer


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

    private val initState = InitState(player)
    private val seekState = SeekState(player)
    private val pauseState = PauseState(player)
    private val resetState = ResetInfoState(player)
    private val bufferState = BufferState(player)
    private val switchState = SwitchSongState(player)
    private val playingState = PlayingState(player)

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
}
