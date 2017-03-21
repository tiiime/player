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
                index = if (index == 0) {
                    playlist.size - 1
                } else {
                    (index - 1) % playlist.size
                }

                targetMusic = playlist[index]
                playing = true
            }
            Actions.ACTION_NEXT_SONG -> return state.clone().apply {
                index = (index + 1) % playlist.size

                targetMusic = playlist[index]
                playing = true
            }
            Actions.ACTION_PAUSE_SONG -> return state.clone().apply {
                playing = false
            }
            Actions.ACTION_PLAY_SONG -> return state.clone().apply {
                if (-1 == index) {
                    index = 0
                    targetMusic = playlist[index]
                }
                playing = true
            }
            Actions.ACTION_UPDATE_INFO -> return state.clone().apply {
                playing = action.content as Boolean
            }
        }
        return state
    }
}
