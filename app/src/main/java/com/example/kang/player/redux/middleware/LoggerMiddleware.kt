package com.example.kang.player.redux.middleware

import com.example.kang.player.model.PlayerState
import com.example.redux.IDispatcher
import com.example.redux.Middleware
import com.example.redux.Store


/**
 * Created by kang on 17-3-13.
 */
class LoggerMiddleware : Middleware<PlayerState> {
    override fun create(store: Store<PlayerState>, nextDispatcher: IDispatcher): IDispatcher {
        return IDispatcher { action ->
            System.out.println(action)
            nextDispatcher.dispatch(action)
        }
    }
}
