package com.tailyou.okplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager

/**
 * 作者：祝文飞（Tailyou）
 * 邮箱：tailyou@163.com
 * 时间：2017/9/6 17:13
 * 描述：接收 PlayService 广播的 Progress 和 Status
 */
abstract class PlayReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_S_2_A_PRG -> {
                val progress = intent.extras[EXTRA_PLAY_PRG] as Int
                onProgressUpdate(progress)
            }
            ACTION_S_2_A_STATUS -> {
                var audioStatus: Int = intent.extras[EXTRA_STATUS] as Int
                when (audioStatus) {
                    STATUS_PREPARED -> {
                        var duration: Int = intent.extras[STATUS_DURATION] as Int
                        onPlayPrepared(duration)
                    }
                    STATUS_COMPLETED -> onPlayCompleted()
                }
            }
            ACTION_INCOMMING_CALL -> {
                var tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
            }
        }
    }

    val phoneStateListener: PhoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String?) {
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    onStateRinging()
                }
                TelephonyManager.CALL_STATE_IDLE -> {
                    onStateIdle()
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    onStateOffHook()
                }
            }
        }
    }

    abstract fun onProgressUpdate(progress: Int)//同步后台播放进度
    abstract fun onPlayPrepared(duration: Int)//播放准备完成，同步状态及音频时长
    abstract fun onPlayCompleted()//播放完成，同步播放完成状态
    abstract fun onStateRinging()//来电-响铃
    abstract fun onStateIdle()//来电-挂断
    abstract fun onStateOffHook()//来电-接听

}