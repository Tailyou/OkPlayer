package com.tailyou.player

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.pawegio.kandroid.onProgressChanged
import com.tailyou.okplayer.OkPlayer
import com.tailyou.okplayer.PlayReceiver
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE)
        const val CODE_ACCESS_PERMISSION = 102
    }

    var isPlaying = false
    var playReceiver = object : PlayReceiver() {
        override fun onStateRinging() {
            Log.e("CALL", "onStateRinging")
        }

        override fun onStateIdle() {
            Log.e("CALL", "onStateIdle")
        }

        override fun onProgressUpdate(progress: Int) {
            seekBarPrg.progress = progress
            tvPlayTime.text = OkPlayer.getFormatPlayTime(progress)
        }

        override fun onPlayPrepared(duration: Int) {
            ivPlayCtrl.setImageResource(R.mipmap.ic_pause)
            tvTotalTime.text = OkPlayer.getFormatPlayTime(duration)
            seekBarPrg.max = duration
            isPlaying = true
        }

        override fun onPlayCompleted() {
            ivPlayCtrl.setImageResource(R.mipmap.ic_play)
            isPlaying = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        OkPlayer.startPlayService(this)
        OkPlayer.registerPlayReceiver(this, playReceiver)
        ivPlayCtrl.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary))
        seekBarPrg.onProgressChanged { progress, fromUser ->
            if (fromUser) {
                OkPlayer.notifyProgress(this, progress)
            }
        }
        ivPlayCtrl.setOnClickListener {
            if (isPlaying) {
                OkPlayer.notifyPause(this)
                ivPlayCtrl.setImageResource(R.mipmap.ic_play)
            } else {
                OkPlayer.notifyPlay(this)
                ivPlayCtrl.setImageResource(R.mipmap.ic_pause)
            }
            isPlaying = !isPlaying
        }
        playMp3()
    }

    override fun onDestroy() {
        super.onDestroy()
        OkPlayer.unregisterPlayReceiver(this, playReceiver)
        OkPlayer.stopPlayService(this)
    }

    @AfterPermissionGranted(CODE_ACCESS_PERMISSION)
    private fun playMp3() {
        if (EasyPermissions.hasPermissions(this, *perms)) {
            Handler().postDelayed({
                val mp3Url = Environment.getExternalStorageDirectory().absolutePath + File.separator + "TailRes/exhibit/0001/CHINESE/0001.mp3"
                OkPlayer.notifyNewMp3(this, mp3Url)
            }, 2000L)
        } else {
            EasyPermissions.requestPermissions(this, "请授予存储访问权限", CODE_ACCESS_PERMISSION, *perms)
        }
    }


}
