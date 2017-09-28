# OkPlayer

1、封装音频播放控制逻辑，减少代码量，提高开发效率；

2、封装之后方便使用和维护。

3、项目地址：https://github.com/Tailyou/OkPlayer


#### 启动PlayService

```kotlin
OkPlayer.startPlayService(this)
```

#### 播放界面实例化并注册PlayReceiver，监听PlayService中音频准备完毕、音频进度更新、音频播放完成。

```kotlin
    var playReceiver = object : PlayReceiver() {
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
```

```kotlin
OkPlayer.registerPlayReceiver(this, playReceiver)
```

#### 通知PlayService播放新音频
```kotlin
OkPlayer.notifyNewMp3(this, mp3Url)
```

#### 通知PlayService播放
```kotlin
OkPlayer.notifyPlay(this)
```

#### 通知PlayService播放暂停
```kotlin
OkPlayer.notifyPause(this)
```

#### 通知PlayService更新进度
```kotlin
OkPlayer.notifyProgress(this, progress)
```



