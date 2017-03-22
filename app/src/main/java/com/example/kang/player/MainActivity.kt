package com.example.kang.player

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.kang.player.model.Music
import com.example.kang.player.model.PlayMode
import com.example.kang.player.model.PlayerState
import com.example.kang.player.model.PlayingState
import com.example.kang.player.redux.creator.PlayerActionCreator
import com.example.kang.player.redux.middleware.LoggerMiddleware
import com.example.kang.player.redux.middleware.PlayerMiddleware
import com.example.kang.player.redux.reducer.PlayerReducer
import com.example.redux.Store
import com.example.redux.Subscriber
import com.google.android.exoplayer2.C
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener, Subscriber {

    private val playerMiddleware = PlayerMiddleware()

    private val playlist = ArrayList<Music>()

    private val DEFAULT_MUSIC_INDEX = -1

    val store: Store<PlayerState> = Store.create(
            PlayerState(playlist, DEFAULT_MUSIC_INDEX, PlayMode.ORDER, PlayingState(0, 0, 0, false), null, null),
            PlayerReducer(),
            LoggerMiddleware(), playerMiddleware)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initClickEvent()
        seekBar.max = PROGRESS_BAR_MAX

        playlist.add(Music("good", 0, Uri.parse("http://192.168.0.108:8080/good.mp3"), null))
        playlist.add(Music("daft", 0, Uri.parse("http://192.168.0.108:8080/daft.mp3"), null))
        playlist.add(Music("marry", 0, Uri.parse("http://192.168.0.108:8080/marry.mp3"), null))
        playlist.add(Music("red", 0, Uri.parse("http://192.168.0.108:8080/red.mp3"), null))
    }

    override fun onStateUpdate(): Unit = with(store.state) {
        play.isSelected = playingState.playing
        currentMusic?.let {
            album.setImageBitmap(currentMusic?.album)
            name.text = currentMusic?.name
        }
        with(playingState) {
            seekBar.progress = progressBarValue(currentPosition, duration)
            seekBar.secondaryProgress = progressBarValue(bufferPosition, duration)
            leftTime.text = (duration - currentPosition).toString()
            currentTime.text = currentPosition.toString()
        }
    }

    override fun onStart() {
        super.onStart()
        playerMiddleware.bindPlayer(this)
    }

    override fun onDestroy() {
        playerMiddleware.releasePlayer(this)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        store.subscribe(this)
    }

    override fun onPause() {
        super.onPause()
        store.unSubscribe(this)
    }

    override fun onClick(v: View) = when (v.id) {
        R.id.album -> {

        }
        R.id.name -> {

        }
        R.id.seekBar -> {

        }
        R.id.previous -> {
            store.dispatch(PlayerActionCreator.prevSong())
        }
        R.id.next -> {
            store.dispatch(PlayerActionCreator.nextSong())
        }
        R.id.play -> {
            if (play.isSelected) {
                store.dispatch(PlayerActionCreator.pause())
            } else {
                store.dispatch(PlayerActionCreator.play())
            }
        }
        else -> {
        }
    }

    private fun progressBarValue(position: Long, duration: Long): Int = when (duration) {
        C.TIME_UNSET, 0L -> 0
        else -> {
            (position * PROGRESS_BAR_MAX / duration).toInt()
        }
    }

    private fun positionValue(progress: Int, duration: Long): Long = if (duration == C.TIME_UNSET) {
        0
    } else {
        duration * progress / PROGRESS_BAR_MAX
    }

    private fun initClickEvent() {
        album.setOnClickListener(this)
        name.setOnClickListener(this)
        currentTime.setOnClickListener(this)
        seekBar.setOnClickListener(this)
        leftTime.setOnClickListener(this)
        previous.setOnClickListener(this)
        play.setOnClickListener(this)
        next.setOnClickListener(this)
    }

    companion object {
        val PROGRESS_BAR_MAX = 1000
    }
}
