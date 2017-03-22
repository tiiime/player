package com.example.kang.player

import android.app.Application
import android.os.StatFs
import com.example.kang.player.util.CacheMonitorInterceptor
import com.example.kang.player.util.OkHttpClientProvider
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


/**
 * Created by kang on 17-3-22.
 */
class PlayerApp : Application() {


    override fun onCreate() {
        super.onCreate()

        OkHttpClientProvider.init(cacheDir)
        Stetho.initializeWithDefaults(this)
    }
}