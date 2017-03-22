package com.example.kang.player.redux.middleware

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import com.example.kang.player.model.Music
import com.example.kang.player.model.PlayerState
import com.example.kang.player.model.PlayingState
import com.example.kang.player.redux.Actions
import com.example.kang.player.redux.creator.PlayerActionCreator
import com.example.kang.player.service.PlayerService
import com.example.redux.Action
import com.example.redux.IDispatcher
import com.example.redux.Middleware
import com.example.redux.Store


/**
 * Created by kang on 17-3-13.
 */
class PlayerMiddleware : Middleware<PlayerState> {
    private var store: Store<PlayerState>? = null

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
        sendUnSubscribeClientMsg()
        context.unbindService(playerServiceConnection)
        messenger = null
    }

    override fun create(store: Store<PlayerState>, nextDispatcher: IDispatcher): IDispatcher {
        this.store = store

        return IDispatcher { action ->
            if (store.state.playlist.isNotEmpty()) dispatchAction(action, nextDispatcher)
        }
    }

    fun dispatchAction(action: Action<Any>, nextDispatcher: IDispatcher) {
        when (action.type) {
            Actions.ACTION_SEEK_SONG -> {
                playerServiceConnection.seekTo(action.content as Long)
                return
            }
            Actions.ACTION_NEXT_SONG -> {
                messenger?.send(Message.obtain(null, PlayerService.MSG_NEXT_SONG))
            }
            Actions.ACTION_PREVIOUS_SONG -> {
                messenger?.send(Message.obtain(null, PlayerService.MSG_PREV_SONG))
            }
            Actions.ACTION_PAUSE_SONG -> {
                messenger?.send(Message.obtain(null, PlayerService.MSG_PAUSE))
            }
            Actions.ACTION_PLAY_SONG -> {
                messenger?.send(Message.obtain(null, PlayerService.MSG_PLAY))
            }
            Actions.ACTION_REQUEST_UPDATE_PLAY_STATE_INFO -> {
                playerServiceConnection.requestPlayerState()
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

            tryInitPlayerService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playerBind = false
        }

        /**
         * set playlist if [PlayerService.playlist] isEmpty
         * else ignore action
         */
        private fun tryInitPlayerService() {
            val msg = Message.obtain(null,PlayerService.MSG_INIT_SERVICE)
            msg.replyTo = localMessenger
            msg.obj = store?.state
            messenger?.send(msg)
        }

        /**
         * request [PlayerService] current playing state
         * in this action we will register our [localMessenger] to [PlayerService]
         */
        fun requestPlayerState() {
            val msg = Message.obtain(null, PlayerService.MSG_GET_PLAYING_INFO)
            msg.replyTo = localMessenger
            messenger?.send(msg)
        }

        fun seekTo(position: Long) {
            val msg = Message.obtain(null, PlayerService.MSG_SEEK)
            msg.obj = position
            messenger?.send(msg)
        }
    }

    /**
     * handle message from [PlayerService]
     */
    internal inner class LocalPlayerHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                PlayerService.MSG_GET_PLAYING_INFO -> {
                    store?.dispatch(PlayerActionCreator.updatePlayStateInfo(msg.obj as PlayingState))
                }
                PlayerService.MSG_SWITCH -> {
                    store?.dispatch(PlayerActionCreator.updateSongInfo(msg.obj as Music))
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    private fun sendUnSubscribeClientMsg() {
        val msg = Message.obtain(null, PlayerService.MSG_UN_SUBSCRIBE_CLIENT)
        msg.replyTo = localMessenger
        messenger?.send(msg)
    }
}
