package com.example.smartalarm

import android.content.Context
import android.media.MediaPlayer

object SoundManager {
    private var mediaPlayer: MediaPlayer? = null

    fun startSound(ctx: Context, sound: Int) {
        stopSound()
        mediaPlayer = MediaPlayer.create(ctx, sound).apply {
            isLooping = true
            start()
        }
    }

    fun stopSound() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}