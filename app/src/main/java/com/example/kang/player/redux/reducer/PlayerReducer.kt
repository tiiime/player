package com.example.kang.player.redux.reducer

import com.example.kang.player.model.PlayerState
import com.example.kang.player.redux.Actions
import com.example.redux.Action
import com.example.redux.IReducer

/**
 * Created by kang on 17-3-13.
 */
class PlayerReducer : IReducer<PlayerState> {
    override fun reduce(state: PlayerState, action: Action<Any>): PlayerState {
        when (action.type) {
            Actions.ACTION_PREVIOUS_SONG -> {

            }
            Actions.ACTION_NEXT_SONG -> {

            }
            Actions.ACTION_PAUSE_SONG -> return state.copy().apply {
                playing = false
            }
            Actions.ACTION_SEEK_SONG -> {

            }
            Actions.ACTION_PLAY_SONG -> return state.copy().apply {
                playing = true
            }
        }
        return state
    }
}
