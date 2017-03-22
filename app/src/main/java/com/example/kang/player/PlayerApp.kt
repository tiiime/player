package com.example.kang.player

import android.app.Application
import com.facebook.stetho.Stetho

/**
 * Created by kang on 17-3-22.
 */
class PlayerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}