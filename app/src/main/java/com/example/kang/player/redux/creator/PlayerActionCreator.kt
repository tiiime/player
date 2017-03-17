package com.example.kang.player.redux.creator

import android.net.Uri
import com.example.kang.player.redux.Actions
import com.example.redux.Action

/**
 * Created by kang on 17-3-17.
 */
object PlayerActionCreator : Action<Any>("", "") {
    fun seekTo(seek: Long) = Action(Actions.ACTION_SEEK_SONG, seek)
    fun nextSong(uri: Uri) = Action(Actions.ACTION_NEXT_SONG, uri)
    fun prevSong(uri: Uri) = Action(Actions.ACTION_PREVIOUS_SONG, uri)
    fun pause() = Action(Actions.ACTION_SEEK_SONG, Any())
    fun play() = Action(Actions.ACTION_PLAY_SONG,Any())
}