package com.chan.volumewidget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chan.volumewidget.databinding.ActivityMainBinding
import com.chan.widget.VerticalSeekBar
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBinding()
        initListener()

    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
    }

    private fun initListener() {
        binding.seekBar.setOnBoxedPointsChangeListener(object : VerticalSeekBar.OnValuesChangeListener{
            override fun onPointsChanged(boxedPoints: VerticalSeekBar?, points: Int) {
                Timber.d("setOnBoxedPointsChangeListener onPointsChanged points : $points")
            }

            override fun onStartTrackingTouch(boxedPoints: VerticalSeekBar?) {
                Timber.d("setOnBoxedPointsChangeListener onStartTrackingTouch")
            }

            override fun onStopTrackingTouch(boxedPoints: VerticalSeekBar?) {
                Timber.d("setOnBoxedPointsChangeListener onStopTrackingTouch")
            }

        })
    }
}