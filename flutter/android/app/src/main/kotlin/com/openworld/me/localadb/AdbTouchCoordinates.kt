package com.openworld.me.localadb

import android.os.Handler
import android.os.Looper
import com.openworld.me.SCREEN_INFO
import com.openworld.me.nZW99cdXQ0COhB2o

/** Read-only geometry adapter; it never changes capture dimensions or service state. */
object AdbTouchCoordinates {
    @JvmStatic fun scale(): Int = SCREEN_INFO.scale.coerceAtLeast(1)

    @JvmStatic fun releaseAccessibilityTouch() {
        Handler(Looper.getMainLooper()).post {
            nZW99cdXQ0COhB2o.ctx?.suspendRemoteTouchForAdb()
        }
    }
}
