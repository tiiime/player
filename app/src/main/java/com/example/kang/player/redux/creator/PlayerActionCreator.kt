package com.example.kang.player.redux.creator

import android.net.Uri
import com.example.kang.player.model.Music
import com.example.kang.player.model.PlayingState
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
    fun updatePlayStateInfo(state: PlayingState) = Action(Actions.ACTION_UPDATE_PLAY_STATE_INFO, state)
    fun updateSongInfo(music: Music) = Action(Actions.ACTION_UPDATE_SONG_INFO, music)
    fun requestUpdatePlayStateInfo() = Action(Actions.ACTION_REQUEST_UPDATE_PLAY_STATE_INFO, Any())
    fun saveFile(uri: Uri) = Action(Actions.ACTION_SAVE_FILE, uri)
}