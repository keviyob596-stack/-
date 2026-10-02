package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SoundManager {
    private var toneGenerator: ToneGenerator? = null
    private var isMuted = false

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        } catch (_: Exception) {
        }
    }

    fun toggleMute(): Boolean {
        isMuted = !isMuted
        return isMuted
    }

    fun isSoundMuted(): Boolean = isMuted

    fun playClick() {
        if (isMuted) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
        } catch (_: Exception) {}
    }

    fun playCoins() {
        if (isMuted) return
        CoroutineScope(Dispatchers.Default).launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_D, 50)
                kotlinx.coroutines.delay(60)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_0, 60)
            } catch (_: Exception) {}
        }
    }

    fun playEntrySound(soundType: String = "DEFAULT") {
        if (isMuted) return
        CoroutineScope(Dispatchers.Default).launch {
            try {
                when (soundType) {
                    "VIP" -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_A, 80)
                        kotlinx.coroutines.delay(90)
                        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_B, 80)
                        kotlinx.coroutines.delay(90)
                        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_C, 120)
                    }
                    "ROYAL" -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_1, 70)
                        kotlinx.coroutines.delay(80)
                        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_5, 70)
                        kotlinx.coroutines.delay(80)
                        toneGenerator?.startTone(ToneGenerator.TONE_DTMF_9, 100)
                    }
                    else -> {
                        toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 100)
                    }
                }
            } catch (_: Exception) {}
        }
    }

    fun playVictory() {
        if (isMuted) return
        CoroutineScope(Dispatchers.Default).launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_3, 100)
                kotlinx.coroutines.delay(110)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_6, 100)
                kotlinx.coroutines.delay(110)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_9, 200)
            } catch (_: Exception) {}
        }
    }

    fun playAlert() {
        if (isMuted) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 120)
        } catch (_: Exception) {}
    }
}
