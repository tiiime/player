package com.example.kang.player.util;

import android.os.StatFs
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by kang on 17-3-22.
 */
object OkHttpClientProvider {

    fun init(cacheDir: File) {
        dir = cacheDir
    }

    var dir: File? = null

    val stat: StatFs by lazy {
        StatFs(dir?.path)
    }

    val bytesAvailable: Long by lazy {
        if (Util.SDK_INT >= 18) {
            stat.blockSizeLong * stat.blockCountLong
        } else {
            (stat.blockSize * stat.blockCount).toLong()
        }
    }

    fun getCacheClient(): OkHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(StethoInterceptor())
            .addInterceptor(CacheMonitorInterceptor())
            .connectTimeout(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS.toLong(), TimeUnit.MILLISECONDS)
            .readTimeout(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS.toLong(), TimeUnit.MILLISECONDS)
            .followSslRedirects(false)
            .cache(Cache(dir, bytesAvailable))
            .build()
}
