package com.example.kang.player.util

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.Renderer
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.Allocator
import com.google.android.exoplayer2.upstream.DefaultAllocator

/**
 * Created by kang on 17-3-27.
 */
class CustomLoadControl : LoadControl {
    val internalLoadControl = DefaultLoadControl(DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))

    override fun onPrepared() {
        internalLoadControl.onPrepared()
    }

    override fun onTracksSelected(renderers: Array<Renderer>, trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
        internalLoadControl.onTracksSelected(renderers, trackGroups, trackSelections)
    }

    override fun onStopped() {
        internalLoadControl.onStopped()
    }

    override fun onReleased() {
        internalLoadControl.onReleased()
    }

    override fun getAllocator(): Allocator? {
        return internalLoadControl.allocator
    }

    override fun shouldStartPlayback(bufferedDurationUs: Long, rebuffering: Boolean): Boolean {
        return internalLoadControl.shouldStartPlayback(bufferedDurationUs, rebuffering)
    }

    override fun shouldContinueLoading(bufferedDurationUs: Long): Boolean {
        return internalLoadControl.shouldContinueLoading(bufferedDurationUs)
    }
}
