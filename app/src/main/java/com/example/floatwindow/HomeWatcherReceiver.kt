package com.example.floatwindow

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.example.floatwindow.receiver.GrantDialogPermissionActivity
import com.example.floatwindow.service.WindowService

class HomeWatcherReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "HomeWatcherReceiver"

        private const val SYSTEM_DIALOG_REASON_KEY = "reason"

        // action 内的某些 reason, 与 intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY) 获取的值进行匹配
        private const val SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps" // home键旁边的最近程序列表键

        private const val SYSTEM_DIALOG_REASON_HOME_KEY = "homekey" // 按下home键

        private const val SYSTEM_DIALOG_REASON_LOCK = "lock" // 锁屏键

        private const val SYSTEM_DIALOG_REASON_ASSIST = "assist" // 某些三星手机的程序列表键
    }

    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action != Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
            return
        }
        when (intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY)) {
            SYSTEM_DIALOG_REASON_HOME_KEY -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(context)) {
                        context?.startService(Intent(context, WindowService::class.java))
                    } else {
                        /**
                         * 这样启动授权界面有一点延时, 授权界面不会马上弹出来, 点击悬浮框也不会马上回到应用
                         */
                        val grantPermissionIntent =
                            Intent(context, GrantDialogPermissionActivity::class.java)
                        grantPermissionIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context?.startActivity(grantPermissionIntent)
                    }
                } else {
                    context?.startService(Intent(context, WindowService::class.java))
                }
            }
            SYSTEM_DIALOG_REASON_RECENT_APPS -> {
                // Home键旁边的显示最近的程序的按钮, 长按Home键 或者 activity 切换键
                Log.i(TAG, "long press home key or activity switch")
            }
            SYSTEM_DIALOG_REASON_LOCK -> {
                // 锁屏，似乎是没有反应，监听Intent.ACTION_SCREEN_OFF这个Action才有用
                Log.i(TAG, "lock")
            }
            SYSTEM_DIALOG_REASON_ASSIST -> {
                // samsung 长按Home键
                Log.i(TAG, "assist")
            }
        }
    }
}