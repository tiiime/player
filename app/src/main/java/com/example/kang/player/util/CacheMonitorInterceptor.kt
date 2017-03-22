package com.example.kang.player.util

import android.util.Log

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.Response

class CacheMonitorInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        Log.d(TAG, "response: " + response)
        Log.d(TAG, "response cache: " + response.cacheResponse())
        Log.d(TAG, "response network: " + response.networkResponse())
        return response
    }

    companion object {
        private val TAG = CacheMonitorInterceptor::class.java.simpleName
    }
}
