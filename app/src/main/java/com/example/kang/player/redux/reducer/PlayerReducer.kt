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
            Actions.ACTION_PREVIOUS_SONG -> return state.clone().apply {
                playing = true
            }
            Actions.ACTION_NEXT_SONG -> return state.clone().apply {
                playing = true
            }
            Actions.ACTION_PAUSE_SONG -> return state.clone().apply {
                playing = false
            }
            Actions.ACTION_PLAY_SONG -> return state.clone().apply {
                playing = true
            }
            Actions.ACTION_UPDATE_INFO -> return state.clone().apply {
                playing = action.content as Boolean
            }
        }
        return state
    }
}
