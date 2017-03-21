package com.example.kang.player.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.id3.ApicFrame
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame

/**
 * Created by kang on 17-3-21.
 */

private val SHORT_TYPE_ALBUM = "TALB"
private val SHORT_TYPE_NAME = "TIT2"
private val SHORT_TYPE_ARTIST = "TPE1"
private val TYPE_ALBUM_ARTIST = "TPE2"

fun Metadata.getArtwork(): Bitmap? = (0..this.length() - 1)
        .map { this.get(it) }
        .filterIsInstance<ApicFrame>()
        .map { it.pictureData }
        .map { BitmapFactory.decodeByteArray(it, 0, it.size) }
        .firstOrNull()

fun Metadata.getSongName(): String? = (0..this.length() - 1)
        .map { this.get(it) }
        .filterIsInstance<TextInformationFrame>()
        .filter { it.id == SHORT_TYPE_NAME }
        .map { it.value }
        .firstOrNull()

fun Metadata.getArtistName(): String? = (0..this.length() - 1)
        .map { this.get(it) }
        .filterIsInstance<TextInformationFrame>()
        .filter { it.id == SHORT_TYPE_ARTIST || it.id == TYPE_ALBUM_ARTIST }
        .map { it.value }
        .firstOrNull()
