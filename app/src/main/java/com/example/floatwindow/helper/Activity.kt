package com.example.floatwindow.helper

import android.app.Activity
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE

/**
 * 判断某个界面是否在前台
 *
 * @return 是否在前台显示
 */
fun Activity.isForeground(): Boolean {
    val className = this.javaClass.name
    if (className.isEmpty()) return false
    val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
    val list = am.getRunningTasks(1)
    if (list != null && list.size > 0) {
        val cpn = list[0].topActivity
        if (className == cpn?.className) return true
    }
    return false
}