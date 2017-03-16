package com.example.kang.player.logic

import com.example.kang.player.logic.state.*
import com.example.kang.player.util.StateMachine

/**
 * Created by kang on 17-3-16.
 *
 */
class MusicStateMachine(name: String) : StateMachine(name) {
    companion object {
        internal val CMD_PLAY = 0x01
        internal val CMD_SEEK = 0x02
        internal val CMD_RESET = 0x03
        internal val CMD_PAUSE = 0x04
        internal val CMD_BUFFER = 0x05
        internal val CMD_SWITCH = 0x06
    }

    val seekState = SeekState()
    val pauseState = PauseState()
    val resetState = ResetInfoState()
    val bufferState = BufferState()
    val switchState = SwitchSongState()
    val playingState = PlayingState()

    /**
     *
     *                       +---------+
     *                       |  PAUSE  |
     *                       +---------+
     *                       /         \
     *           +----------+           +---------+
     *           |   SEEK   |           |  RESET  |
     *           +----------+           +---------+
     *                |                      |
     *           +----------+           +---------+
     *           |  BUFFER  |           |  SWITCH |
     *           +----------+           +---------+
     *                |
     *           +----------+
     *           | PLAYING  |
     *           +----------+
     */
    init {
        addState(pauseState, null)
            addState(seekState, pauseState)
                addState(bufferState, seekState)
                    addState(playingState, bufferState)
            addState(resetState, pauseState)
                addState(switchState, resetState)

        setInitialState(pauseState)
    }
}
