package com.tailyou.okplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * 作者：祝文飞（Tailyou）
 * 邮箱：tailyou@163.com
 * 时间：2017/9/6 17:13
 * 描述：接收 PlayService 广播的 Progress 和 Status
 */
abstract class PlayReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            PlayService.ACTION_PRG_S_2_A -> {
                val progress = intent.extras[PlayService.EXTRA_PLAY_PRG] as Int
                onProgressUpdate(progress)
            }
            PlayService.ACTION_PLAY_STATUS -> {
                var playCtrl: Int = intent.extras[PlayService.EXTRA_PLAY_STATUS] as Int
                when (playCtrl) {
                    PlayService.EXTRA_STATUS_PREPARED -> {
                        var duration: Int = intent.extras[PlayService.EXTRA_PLAY_DURATION] as Int
                        onPlayPrepared(duration)
                    }
                    PlayService.EXTRA_STATUS_COMPLETED -> onPlayCompleted()
                }
            }
        }
    }

    abstract fun onProgressUpdate(progress: Int)//同步后台播放进度
    abstract fun onPlayPrepared(duration: Int)//播放准备完成，同步状态及音频时长
    abstract fun onPlayCompleted()//播放完成，同步播放完成状态

}