package com.example.kang.player.redux.middleware

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
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
    private var musicIntent: Intent? = null
    private var messenger: Messenger? = null
    private val localMessenger = Messenger(LocalPlayerHandler())
    private val playerServiceConnection = PlayerServiceConnection()

    fun bindPlayer(context: Context) {
        if (musicIntent == null) {
            musicIntent = Intent(context, PlayerService::class.java)
            context.bindService(musicIntent, playerServiceConnection, Context.BIND_AUTO_CREATE)
            context.startService(musicIntent)
        }
    }

    fun releasePlayer(context: Context) {
        context.unbindService(playerServiceConnection)
        messenger = null
    }

    override fun create(store: Store<PlayerState>, nextDispatcher: IDispatcher): IDispatcher {
        return IDispatcher { action ->
            if (store.state.playlist.isNotEmpty()) dispatchAction(store, action, nextDispatcher)
        }
    }

    fun dispatchAction(store: Store<PlayerState>, action: Action<Any>, nextDispatcher: IDispatcher) {
        when (action.type) {
            Actions.ACTION_PAUSE_SONG -> {
                messenger?.send(Message.obtain(null, PlayerService.MSG_PAUSE))
            }
            Actions.ACTION_SEEK_SONG -> {
                return
            }
            Actions.ACTION_PLAY_SONG -> {
                messenger?.send(Message.obtain(null, PlayerService.MSG_PLAY))
            }
            Actions.ACTION_SWITCH_SONG -> {
                val music = store.state.playlist[action.content as Int]

                val msg = Message.obtain(null, PlayerService.MSG_SWITCH)
                msg.obj = music.uri

                messenger?.send(msg)
                return
            }
        }
        nextDispatcher.dispatch(action)
    }

    internal inner class PlayerServiceConnection : android.content.ServiceConnection {
        var playerBind = false

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            messenger = Messenger(service)
            playerBind = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playerBind = false
        }
    }

    internal inner class LocalPlayerHandler : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
        }
    }
}
