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

    companion object {
        //新音频
        const val ACTION_NEW_MP3 = "ACTION_NEW_MP3"
        const val EXTRA_NEW_MP3 = "EXTRA_NEW_MP3"
        //播放进度
        const val ACTION_PRG_A_2_S = "ACTION_PRG_A_2_S"
        const val ACTION_PRG_S_2_A = "ACTION_PRG_S_2_A"
        const val EXTRA_PLAY_PRG = "EXTRA_PLAY_PRG"
        //MediaPlayer（Prepared，Completion）
        const val ACTION_PLAY_STATUS = "ACTION_PLAY_STATUS"
        const val EXTRA_PLAY_STATUS = "EXTRA_PLAY_STATUS"
        const val EXTRA_STATUS_COMPLETED = 2001
        const val EXTRA_STATUS_PREPARED = 2002
        const val EXTRA_PLAY_DURATION = "EXTRA_PLAY_DURATION"
        //播放控制（播放，暂停）
        const val ACTION_PLAY_CTRL = "ACTION_PLAY_CTRL"
        const val EXTRA_PLAY_CTRL = "EXTRA_PLAY_CTRL"
        const val EXTRA_CTRL_PLAY = 1001
        const val EXTRA_CTRL_PAUSE = 1002
        //update frequency
        const val WHAT_CHANGE_PLAY_PROGRESS = 1
        const val HANDLER_DELAY = 1000L
    }

    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mPlayReceiver: PlayReceiver
    private lateinit var mPlayHandler: PlayHandler
    private var isPause = false

    inner class PlayHandler : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            notifyProgress()
            sendEmptyMessageDelayed(WHAT_CHANGE_PLAY_PROGRESS, HANDLER_DELAY)
        }
    }

    inner class PlayReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_NEW_MP3 -> {
                    var mp3Path: String = intent.extras[EXTRA_NEW_MP3] as String
                    onNewMp3(mp3Path)
                }
                ACTION_PRG_A_2_S -> {
                    val currentPosition = intent.extras[PlayService.EXTRA_PLAY_PRG] as Int
                    mMediaPlayer.seekTo(currentPosition)
                }
                ACTION_PLAY_CTRL -> {
                    var playCtrl: Int = intent.extras[EXTRA_PLAY_CTRL] as Int
                    when (playCtrl) {
                        EXTRA_CTRL_PLAY -> onPlay()
                        EXTRA_CTRL_PAUSE -> onPause()
                    }
                }
            }
        }
    }

    fun onNewMp3(mp3Path: String) {
        mMediaPlayer.reset()
        mMediaPlayer.setOnCompletionListener { notifyCompleted() }
        mMediaPlayer.setOnPreparedListener { notifyPrepared() }
        mMediaPlayer.setDataSource(mp3Path)
        mMediaPlayer.prepareAsync()
    }

    fun onPlay() {
        mPlayHandler.sendEmptyMessage(WHAT_CHANGE_PLAY_PROGRESS)
        mMediaPlayer.start()
        isPause = false
    }

    fun onPause() {
        mPlayHandler.removeMessages(WHAT_CHANGE_PLAY_PROGRESS)
        mMediaPlayer.pause()
        isPause = true
    }

    //通知播放活动（Activity）-准备完毕
    private fun notifyPrepared() {
        var duration = mMediaPlayer.duration
        var playStatus = Intent(ACTION_PLAY_STATUS)
        playStatus.putExtra(EXTRA_PLAY_STATUS, EXTRA_STATUS_PREPARED)
        playStatus.putExtra(EXTRA_PLAY_DURATION, duration)
        sendBroadcast(playStatus)
        if (!isPause) onPlay()
    }

    //通知播放活动（Activity）-更新进度
    private fun notifyProgress() {
        var currentTime = mMediaPlayer.currentPosition
        var playPrg = Intent(ACTION_PRG_S_2_A)
        playPrg.putExtra(EXTRA_PLAY_PRG, currentTime)
        sendBroadcast(playPrg)
    }

    //通知播放活动（Activity）-播放完成
    private fun notifyCompleted() {
        var playStatus = Intent(ACTION_PLAY_STATUS)
        playStatus.putExtra(EXTRA_PLAY_STATUS, EXTRA_STATUS_COMPLETED)
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
        filter.addAction(ACTION_NEW_MP3)
        filter.addAction(ACTION_PRG_A_2_S)
        filter.addAction(ACTION_PLAY_CTRL)
        registerReceiver(mPlayReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mPlayReceiver)
        mMediaPlayer.stop()
        mMediaPlayer.release()
    }

}