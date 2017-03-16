package com.example.kang.player.logic

import com.example.kang.player.logic.state.*
import com.example.kang.player.util.StateMachine

/**
 * Created by kang on 17-3-16.
 *
 */
class MusicStateMachine(name: String) : StateMachine(name) {
    val pauseState = PauseState()
    val seekState = SeekState()
    val playingState = PlayingState()
    val resetState = ResetInfoState()
    val bufferState = BufferState()
    val switchState = SwitchSongState();

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
