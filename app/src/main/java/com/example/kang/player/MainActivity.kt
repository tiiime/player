package com.example.kang.player

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.kang.player.model.Music
import com.example.kang.player.model.PlayMode
import com.example.kang.player.model.PlayerState
import com.example.kang.player.redux.creator.PlayerActionCreator
import com.example.kang.player.redux.middleware.LoggerMiddleware
import com.example.kang.player.redux.middleware.PlayerMiddleware
import com.example.kang.player.redux.reducer.PlayerReducer
import com.example.redux.Store
import com.example.redux.Subscriber
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener, Subscriber {

    val store: Store<PlayerState> = Store.create(
            PlayerState(ArrayList<Music>(), 0, PlayMode.ORDER, false),
            PlayerReducer(),
            LoggerMiddleware(), PlayerMiddleware())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initClickEvent()
    }

    override fun onStateUpdate() = with(store.state) {
        play.isSelected = playing
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
        R.id.currentTime -> {

        }
        R.id.seekBar -> {

        }
        R.id.leftTime -> {

        }
        R.id.previous -> {
            store.dispatch(PlayerActionCreator.prevSong())
        }
        R.id.play -> {
            if (play.isSelected) {
                store.dispatch(PlayerActionCreator.pause())
            } else {
                store.dispatch(PlayerActionCreator.play())
            }
        }
        R.id.next -> {
        }
        else -> {
        }
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
}
