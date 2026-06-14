package com.example.ui

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

object AudioSynthesizer {
    private const val SAMPLE_RATE = 22050

    fun playCorrect() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                // Play sequential rising arpeggio: C5 (523.25Hz), E5 (659.25Hz), G5 (783.99Hz), C6 (1046.50Hz)
                val notes = floatArrayOf(523.25f, 659.25f, 783.99f, 1046.50f)
                val durationMs = 120
                
                notes.forEachIndexed { index, freq ->
                    val noteDelay = index * 80
                    launch {
                        delay(noteDelay.toLong())
                        generateAndPlayTone(freq, durationMs, 0.15f, "triangle")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playIncorrect() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                // Play a low, brief warning buzzer
                generateAndPlayToneCombined(180f, 175f, 300, 0.20f)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playCelebration() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                // Play a majestic celebratory progression
                val majorChord = floatArrayOf(523.25f, 587.33f, 659.25f, 783.99f, 880.00f, 1046.50f)
                majorChord.forEachIndexed { index, freq ->
                     val noteDelay = index * 100
                     launch {
                         delay(noteDelay.toLong())
                         generateAndPlayTone(freq, 400, 0.12f, "sine")
                         generateAndPlayTone(freq * 1.5f, 300, 0.05f, "triangle") // perfect fifth harmony
                     }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun generateAndPlayTone(frequency: Float, durationMs: Int, volume: Float, type: String = "sine") {
        val numSamples = (durationMs * SAMPLE_RATE / 1000)
        val generatedSnd = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val sampleVal = when (type) {
                "triangle" -> {
                    val period = 1.0 / frequency
                    val progress = (t % period) / period
                    if (progress < 0.25) (progress * 4.0)
                    else if (progress < 0.75) (2.0 - progress * 4.0)
                    else (progress * 4.0 - 4.0)
                }
                else -> {
                    sin(2.0 * Math.PI * frequency * t)
                }
            }
            
            // Fade-in and fade-out envelope to prevent speaker clicking
            val envelope = when {
                i < numSamples * 0.1 -> i / (numSamples * 0.1)
                i > numSamples * 0.7 -> (numSamples - i) / (numSamples * 0.3)
                else -> 1.0
            }
            
            generatedSnd[i] = (sampleVal * envelope * Short.MAX_VALUE * volume).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        playAudioTrack(generatedSnd)
    }

    private fun generateAndPlayToneCombined(freq1: Float, freq2: Float, durationMs: Int, volume: Float) {
        val numSamples = (durationMs * SAMPLE_RATE / 1000)
        val generatedSnd = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            
            val p1 = 1.0 / freq1
            val progress = (t % p1) / p1
            val sawVal = 2.0 * progress - 1.0
            
            val sineVal = sin(2.0 * Math.PI * freq2 * t)
            val blendedVal = (sawVal * 0.5 + sineVal * 0.5)

            val envelope = when {
                i < numSamples * 0.1 -> i / (numSamples * 0.1)
                i > numSamples * 0.6 -> (numSamples - i) / (numSamples * 0.4)
                else -> 1.0
            }

            generatedSnd[i] = (blendedVal * envelope * Short.MAX_VALUE * volume).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        playAudioTrack(generatedSnd)
    }

    private fun playAudioTrack(samples: ShortArray) {
        var track: AudioTrack? = null
        try {
            val bufferSize = samples.size * 2
            
            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(samples, 0, samples.size)
            track.play()
            
            val durationMs = (samples.size.toFloat() / SAMPLE_RATE * 1000).toLong()
            Thread.sleep(durationMs + 50)
            track.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                track?.release()
            } catch (_: Exception) {}
        }
    }
}
