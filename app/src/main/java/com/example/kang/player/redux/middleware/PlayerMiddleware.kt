package com.example.kang.player.redux.middleware

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.example.kang.player.model.PlayerState
import com.example.kang.player.redux.Actions
import com.example.kang.player.service.PlayerService
import com.example.redux.Action
import com.example.redux.IDispatcher
import com.example.redux.Middleware
import com.example.redux.Store


/**
 * Created by kang on 17-3-13.
 */
class PlayerMiddleware : Middleware<PlayerState> {
    var musicIntent: Intent? = null
    var playerService: PlayerService? = null

    fun bindPlayer(context: Context) {
        if (musicIntent == null) {
            musicIntent = Intent(context, PlayerService::class.java)
            context.bindService(musicIntent, PlayerServiceConnection(), Context.BIND_AUTO_CREATE)
            context.startService(musicIntent)
        }
    }

    fun releasePlayer(context: Context) {
        context.stopService(musicIntent)
        playerService = null
    }

    override fun create(store: Store<PlayerState>, nextDispatcher: IDispatcher): IDispatcher {
        return IDispatcher { action ->
            if (store.state.playlist.isNotEmpty()) dispatchAction(store, action, nextDispatcher)
        }
    }

    fun dispatchAction(store: Store<PlayerState>, action: Action<Any>, nextDispatcher: IDispatcher) {
        when (action.type) {
            Actions.ACTION_PAUSE_SONG -> {
                playerService?.pause()
            }
            Actions.ACTION_SEEK_SONG -> {
                return
            }
            Actions.ACTION_PLAY_SONG -> {
                playerService?.play()
            }
            Actions.ACTION_SWITCH_SONG -> {
                val music = store.state.playlist[action.content as Int]
                playerService?.switch(music.uri)
                return
            }
        }
        nextDispatcher.dispatch(action)
    }

    internal inner class PlayerServiceConnection : android.content.ServiceConnection {
        var playerBind = false

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PlayerService.PlayerBinder
            playerService = binder.getService()
            playerBind = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playerBind = false
        }
    }
}
