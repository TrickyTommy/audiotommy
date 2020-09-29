package com.example.myaudiorecorder

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.IOException
import java.util.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    lateinit var status: TextView
    lateinit var btnPlay: ImageView
    lateinit var btnStop: ImageView
    lateinit var btnRecordingStart: ImageView
    lateinit var btnRecordingStop: Button
    private var permissions: Array<String> = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.RECORD_AUDIO
    )
    var pathSave: String = ""
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var mediaPlayer: MediaPlayer
    private var REQUEST_PERMISSION_CODE: Int = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!checkPermissionFromDevice()){
            requestPermission()
        }
        setContentView(R.layout.activity_main)
        btnPlay = findViewById(R.id.play)
        btnStop = findViewById(R.id.stop)
        btnRecordingStart = findViewById(R.id.btnRecord)
        btnRecordingStop = findViewById(R.id.btnStopRecord)
        status = findViewById(R.id.textView2)


        btnRecordingStart.setOnClickListener {
            if (checkPermissionFromDevice()) {
                pathSave =
                    "${externalCacheDir?.absolutePath}/myRecording.3gp"
                setMediaRecorder()
                try {
                    mediaRecorder.prepare()
                    mediaRecorder.start()

                } catch (e: IOException) {
                    e.printStackTrace()
                }
                btnPlay.isEnabled = false
                btnStop.isEnabled = false
                status.text = "Recording..."
            } else {
                requestPermission()
            }
            btnRecordingStop.setOnClickListener {
                mediaRecorder.stop()
                btnRecordingStop.isEnabled = false
                btnPlay.isEnabled = true
                btnRecordingStop.isEnabled = true
                btnStop.isEnabled = false
                status.text="Stopping Recording "
            }
            btnPlay.setOnClickListener {
                btnStop.isEnabled = true
                btnRecordingStop.isEnabled = false
                btnRecordingStart.isEnabled = false
                mediaPlayer = MediaPlayer()
                try {
                    mediaPlayer.setDataSource(pathSave)
                    mediaPlayer.prepare()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                mediaPlayer.start()
                status.text = "Playing...."
            }
            btnStop.setOnClickListener {
                btnRecordingStop.isEnabled = false
                btnRecordingStart.isEnabled = true
                btnStop.isEnabled = false
                btnPlay.isEnabled = true
                if (mediaPlayer != null) {
                    mediaPlayer.stop()
                    mediaPlayer.release()
                    status.text="Stop Playing"
                    setMediaRecorder()

                }
            }

        }
    }

    private fun setMediaRecorder() {
        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        mediaRecorder.setOutputFile(pathSave)

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE)
    }

    private fun checkPermissionFromDevice(): Boolean {
        val write_external_storage_result: Int = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val record_audio_result: Int =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSION_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

}


