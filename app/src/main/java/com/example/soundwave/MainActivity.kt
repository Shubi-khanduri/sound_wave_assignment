package com.example.soundwave

import android.content.pm.PackageManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.soundwave.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var binding: ActivityMainBinding
    private val audioList = listOf(
        R.raw.sample_audio,
        R.raw.sample_audio2,
        R.raw.sample_audio3
    )
    private var currentAudioIndex = 0

    private val RECORD_AUDIO_PERMISSION_CODE = 1

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.RECORD_AUDIO),
            RECORD_AUDIO_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                playAudio(currentAudioIndex) // Start audio playback if permission is granted
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (checkPermissions()) {
            playAudio(currentAudioIndex)
        } else {
            requestPermissions()
        }

        binding.play.setOnClickListener {

            mediaPlayer.start()
            binding.barVisualizer.setDensity(50f)
            binding.barVisualizer.setPlayer(mediaPlayer.audioSessionId)
            binding.play.visibility = View.GONE
            binding.pause.visibility = View.VISIBLE
        }
        binding.pause.setOnClickListener {
            mediaPlayer.pause()
            binding.pause.visibility = View.GONE
            binding.play.visibility= View.VISIBLE
        }

        binding.next.setOnClickListener {
            currentAudioIndex = (currentAudioIndex + 1) % audioList.size
            playAudio(currentAudioIndex)
        }

        binding.previous.setOnClickListener {
            currentAudioIndex = if (currentAudioIndex - 1 < 0) audioList.size - 1 else currentAudioIndex - 1
            playAudio(currentAudioIndex)
        }
    }
    private fun playAudio(index: Int) {
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        mediaPlayer = MediaPlayer.create(this, audioList[index])
        binding.barVisualizer.setColor(ContextCompat.getColor(this, R.color.white))
        binding.barVisualizer.setPlayer(mediaPlayer.audioSessionId)
        mediaPlayer.start()
        binding.play.visibility = View.GONE
        binding.pause.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.barVisualizer.release()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }

}