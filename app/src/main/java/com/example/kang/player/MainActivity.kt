package com.example.kang.player

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.SeekBar
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

class MainActivity : AppCompatActivity(), View.OnClickListener, Subscriber, SeekBar.OnSeekBarChangeListener {

    private var duration = 0L

    private var dragging = false

    private val playerMiddleware = PlayerMiddleware()

    private val playlist = ArrayList<Music>()

    private val DEFAULT_MUSIC_INDEX = -1

    private val store: Store<PlayerState> = Store.create(
            PlayerState(playlist, DEFAULT_MUSIC_INDEX, PlayMode.ORDER, PlayingState(0, 0, 0, false), null, null),
            PlayerReducer(),
            LoggerMiddleware(), playerMiddleware)

    private val updateProgressAction: Runnable by lazy {
        Runnable {
            updateProgress()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initClickEvent()
        initViewsProperty()

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
            if (!dragging) {
                seekBar.progress = progressBarValue(currentPosition, duration)
            }

            seekBar.secondaryProgress = progressBarValue(bufferPosition, duration)
            leftTime.text = stringForTime(duration - currentPosition)
            currentTime.text = stringForTime(currentPosition)

            this@MainActivity.duration = duration
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
        seekBar.removeCallbacks(updateProgressAction)
        store.unSubscribe(this)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        dragging = false
        seekTo(seekBar.progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        dragging = true
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser && !dragging) {
            seekTo(progress)
        }
    }

    private fun seekTo(progress: Int) {
        val position = positionValue(progress, duration)
        store.dispatch(PlayerActionCreator.seekTo(position))
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
                seekBar.removeCallbacks(updateProgressAction)
                store.dispatch(PlayerActionCreator.pause())
            } else {
                updateProgress()
                store.dispatch(PlayerActionCreator.play())
            }
        }
        else -> {
        }
    }

    private fun updateProgress() {
        store.dispatch(PlayerActionCreator.requestUpdatePlayStateInfo())
        seekBar.removeCallbacks(updateProgressAction)
        seekBar.postDelayed(updateProgressAction, 1000L)
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

    private val formatBuilder = StringBuilder()
    private val formatter = Formatter(formatBuilder, Locale.getDefault())

    private fun stringForTime(timeMs: Long): String {
        var timeMs = timeMs
        if (timeMs == C.TIME_UNSET) {
            timeMs = 0
        }
        val totalSeconds = (timeMs + 500) / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        formatBuilder.setLength(0)
        return if (hours > 0)
            formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        else
            formatter.format("%02d:%02d", minutes, seconds).toString()
    }

    private fun initViewsProperty() {
        seekBar.max = PROGRESS_BAR_MAX
        seekBar.setOnSeekBarChangeListener(this)
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
