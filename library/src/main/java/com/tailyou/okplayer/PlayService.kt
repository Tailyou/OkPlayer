package com.tailyou.okplayer

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Message

/**
 * 作者：祝文飞（Tailyou）
 * 邮箱：tailyou@163.com
 * 时间：2017/8/19 9:26
 * 描述：语音播放服务
 */
class PlayService : Service() {

    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mPlayReceiver: PlayReceiver
    private lateinit var mPlayHandler: PlayHandler
    private var isPause = false

    inner class PlayHandler : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            notifyProgress()
            sendEmptyMessageDelayed(Constants.WHAT_CHANGE_PLAY_PROGRESS, Constants.HANDLER_DELAY)
        }
    }

    inner class PlayReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Constants.ACTION_A_2_S_MP3 -> {
                    var mp3Path: String = intent.extras[Constants.EXTRA_MP3_PATH] as String
                    onNewMp3(mp3Path)
                }
                Constants.ACTION_A_2_S_PRG -> {
                    val currentPosition = intent.extras[Constants.EXTRA_PLAY_PRG] as Int
                    mMediaPlayer.seekTo(currentPosition)
                }
                Constants.ACTION_A_2_S_CTRL -> {
                    var playCtrl: Int = intent.extras[Constants.EXTRA_PLAY_CTRL] as Int
                    when (playCtrl) {
                        Constants.PLAY_CTRL_PLAY -> onPlay()
                        Constants.PLAY_CTRL_PAUSE -> onPause()
                    }
                }
            }
        }
    }

    fun onNewMp3(mp3Path: String) {
        isPause = false
        mMediaPlayer.reset()
        mMediaPlayer.setOnCompletionListener { notifyCompleted() }
        mMediaPlayer.setOnPreparedListener { notifyPrepared() }
        mMediaPlayer.setDataSource(mp3Path)
        mMediaPlayer.prepareAsync()
    }

    fun onPlay() {
        mPlayHandler.sendEmptyMessage(Constants.WHAT_CHANGE_PLAY_PROGRESS)
        mMediaPlayer.start()
    }

    fun onPause() {
        mPlayHandler.removeMessages(Constants.WHAT_CHANGE_PLAY_PROGRESS)
        mMediaPlayer.pause()
        isPause = true
    }

    //通知播放活动（Activity）-准备完毕
    private fun notifyPrepared() {
        if (!isPause) {
            var duration = mMediaPlayer.duration
            var playStatus = Intent(Constants.ACTION_S_2_A_STATUS)
            playStatus.putExtra(Constants.EXTRA_STATUS, Constants.STATUS_PREPARED)
            playStatus.putExtra(Constants.STATUS_DURATION, duration)
            sendBroadcast(playStatus)
            onPlay()
        }
    }

    //通知播放活动（Activity）-更新进度
    private fun notifyProgress() {
        var currentTime = mMediaPlayer.currentPosition
        var playPrg = Intent(Constants.ACTION_S_2_A_PRG)
        playPrg.putExtra(Constants.EXTRA_PLAY_PRG, currentTime)
        sendBroadcast(playPrg)
    }

    //通知播放活动（Activity）-播放完成
    private fun notifyCompleted() {
        var playStatus = Intent(Constants.ACTION_S_2_A_STATUS)
        playStatus.putExtra(Constants.EXTRA_STATUS, Constants.STATUS_COMPLETED)
        sendBroadcast(playStatus)
    }

    override fun onBind(p0: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mPlayHandler = PlayHandler()
        mMediaPlayer = MediaPlayer()
        mPlayReceiver = PlayReceiver()
        var filter = IntentFilter()
        filter.addAction(Constants.ACTION_A_2_S_MP3)
        filter.addAction(Constants.ACTION_A_2_S_PRG)
        filter.addAction(Constants.ACTION_A_2_S_CTRL)
        registerReceiver(mPlayReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mPlayReceiver)
        mMediaPlayer.stop()
        mMediaPlayer.release()
    }

}