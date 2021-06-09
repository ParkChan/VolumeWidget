package com.chan.volumewidget

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chan.volumewidget.databinding.ActivityMainBinding
import com.chan.widget.VerticalSeekBar
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val audioManager: AudioManager by lazy {
        getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBinding()
        initSeekBar()
        initListener()

    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
    }

    private fun initSeekBar() {
        val maxMusicVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val presentMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        binding.seekBar.max = maxMusicVolume
        binding.seekBar.value = presentMusicVolume

        Timber.d("initSeekBar musicMaxVolume : $maxMusicVolume")
        Timber.d("initSeekBar presentVolume : $presentMusicVolume")
    }

    private fun initListener() {
        var point = 0
        binding.seekBar.onValuesChangeListener = object :
            VerticalSeekBar.OnValuesChangeListener {

            override fun onPointsChanged(boxedPoints: VerticalSeekBar?, points: Int) {
                Timber.d("setOnBoxedPointsChangeListener onPointsChanged points : $points")
                point = points
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    points,
                    AudioManager.FLAG_PLAY_SOUND
                )
            }

            override fun onStartTrackingTouch(boxedPoints: VerticalSeekBar?) {
                Timber.d("setOnBoxedPointsChangeListener onStartTrackingTouch")
            }

            override fun onStopTrackingTouch(boxedPoints: VerticalSeekBar?) {
                Timber.d("setOnBoxedPointsChangeListener onStopTrackingTouch")
                binding.seekBar.value = point
            }
        }
    }
}