package com.example.kang.player.redux.middleware

import com.example.kang.player.model.PlayerState
import com.example.kang.player.redux.Actions
import com.example.redux.Action
import com.example.redux.IDispatcher
import com.example.redux.Middleware
import com.example.redux.Store


/**
 * Created by kang on 17-3-13.
 */
class PlayerMiddleware : Middleware<PlayerState> {

    override fun create(store: Store<PlayerState>, nextDispatcher: IDispatcher): IDispatcher {
        return IDispatcher { action ->
            dispatchAction(action, nextDispatcher)
        }
    }

    fun dispatchAction(action: Action<Any>, nextDispatcher: IDispatcher) {

        when (action.type) {
            Actions.ACTION_PAUSE_SONG -> {
            }
            Actions.ACTION_SEEK_SONG -> {
                return
            }
            Actions.ACTION_PLAY_SONG -> {
            }
            Actions.ACTION_SWITCH_SONG -> {
                return
            }
        }
        nextDispatcher.dispatch(action)
    }
}
