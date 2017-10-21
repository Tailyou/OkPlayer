package com.tailyou.okplayer

import android.content.Context
import android.content.Intent
import android.content.IntentFilter

/**
 * 作者：祝文飞（Tailyou）
 * 邮箱：tailyou@163.com
 * 时间：2017/9/6 17:01
 * 描述：音频播放工具类
 */
object OkPlayer {

    //启动音频播放服务
    fun startPlayService(context: Context) {
        context.startService(Intent(context, PlayService::class.java))
    }

    //停止音频播放服务
    fun stopPlayService(context: Context) {
        context.stopService(Intent(context, PlayService::class.java))
    }

    //注册播放（进度、状态）接收器
    fun registerPlayReceiver(context: Context, playReceiver: PlayReceiver) {
        var filter = IntentFilter()
        filter.addAction(ACTION_S_2_A_PRG)
        filter.addAction(ACTION_S_2_A_STATUS)
        filter.addAction(ACTION_INCOMMING_CALL)
        context.registerReceiver(playReceiver, filter)
    }

    //取消注册
    fun unregisterPlayReceiver(context: Context, playReceiver: PlayReceiver) {
        context.unregisterReceiver(playReceiver)
    }

    //通知PlayService-新音频
    fun notifyNewMp3(context: Context, mp3Url: String) {
        var newMp3 = Intent(ACTION_A_2_S_MP3)
        newMp3.putExtra(EXTRA_MP3_PATH, mp3Url)
        context.sendBroadcast(newMp3)
    }

    //通知PlayService-播放
    fun notifyPlay(context: Context) {
        var playCtrl = Intent(ACTION_A_2_S_CTRL)
        playCtrl.putExtra(EXTRA_PLAY_CTRL, PLAY_CTRL_PLAY)
        context.sendBroadcast(playCtrl)
    }

    //通知PlayService-更新进度
    fun notifyProgress(context: Context, progress: Int) {
        var intentPrg = Intent(ACTION_A_2_S_PRG)
        intentPrg.putExtra(EXTRA_PLAY_PRG, progress)
        context.sendBroadcast(intentPrg)
    }

    //通知PlayService-暂停
    fun notifyPause(context: Context) {
        var playCtrl = Intent(ACTION_A_2_S_CTRL)
        playCtrl.putExtra(EXTRA_PLAY_CTRL, PLAY_CTRL_PAUSE)
        context.sendBroadcast(playCtrl)
    }

    //格式化播放时间
    fun getFormatPlayTime(progress: Int): String {
        val min = progress / 1000 / 60
        val sec = progress / 1000 % 60
        val formatTime = String.format("%02d:%02d", min, sec)
        return formatTime
    }

}