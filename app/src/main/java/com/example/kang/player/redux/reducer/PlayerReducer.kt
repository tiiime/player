package com.example.kang.player.redux.reducer

import com.example.kang.player.model.PlayerState
import com.example.redux.Action
import com.example.redux.IReducer

/**
 * Created by kang on 17-3-13.
 */
class PlayerReducer : IReducer<PlayerState> {
    override fun reduce(state: PlayerState, action: Action<Any>): PlayerState {
        return state
    }
}
