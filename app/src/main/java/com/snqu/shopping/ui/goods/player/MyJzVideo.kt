package com.snqu.shopping.ui.goods.player

import android.content.Context
import android.util.AttributeSet
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd

/**
 * desc:
 * time: 2019/8/12
 * @author 银进
 */
open class MyJzVideo : JzvdStd {
    open var callBack: CallBack? = null
    open var completion: Completion? = null
    open var isVolume: Boolean? = false
    open var canEnterFullScreen: Boolean? = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onPrepared() {
        super.onPrepared()
        if (screen == Jzvd.SCREEN_FULLSCREEN) {
            mediaInterface.setVolume(1f, 1f)
        } else {
            if (isVolume!!) {
                mediaInterface.setVolume(1f, 1f)
            } else {
                mediaInterface.setVolume(0f, 0f)
            }
        }
    }

    override fun onAutoCompletion() {
        super.onAutoCompletion()
        if (null != completion) {
            completion?.onComplete()
        }
    }

    /**
     * 进入全屏模式的时候关闭静音模式
     */
    override fun gotoScreenFullscreen() {
        if (!canEnterFullScreen!!) {
            return
        }
        if (callBack != null) {
            callBack?.gotoScreenFullscreen()
        } else {
            super.gotoScreenFullscreen()
        }

    }

    override fun setScreenFullscreen() {
        super.setScreenFullscreen()
        if (mediaInterface != null)
            if (isVolume!!) {
                mediaInterface.setVolume(1f, 1f)
            } else {
                mediaInterface.setVolume(0f, 0f)
            }
    }

    override fun setScreenNormal() {
        super.setScreenNormal()
        if (mediaInterface != null) {
            if (isVolume!!) {
                mediaInterface.setVolume(1f, 1f)
            } else {
                mediaInterface.setVolume(0f, 0f)
            }
        }
    }

}

interface CallBack {
    fun gotoScreenFullscreen()
}

interface Completion {
    fun onComplete()
}