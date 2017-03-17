package com.example.kang.player.redux.creator

import android.net.Uri
import com.example.kang.player.redux.Actions
import com.example.redux.Action

/**
 * Created by kang on 17-3-17.
 */
object PlayerActionCreator : Action<Any>("", "") {
    fun seekTo(seek: Long) = Action(Actions.ACTION_SEEK_SONG, seek)
    fun nextSong() = Action(Actions.ACTION_NEXT_SONG, Any())
    fun prevSong() = Action(Actions.ACTION_PREVIOUS_SONG, Any())
    fun pause() = Action(Actions.ACTION_PAUSE_SONG, Any())
    fun play() = Action(Actions.ACTION_PLAY_SONG,Any())
    fun switch(uri: Uri) = Action(Actions.ACTION_SWITCH_SONG, uri)
}