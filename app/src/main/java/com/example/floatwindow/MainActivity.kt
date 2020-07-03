package com.example.floatwindow

import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.floatwindow.helper.isForeground
import com.example.floatwindow.service.WindowService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_DIALOG_PERMISSION = 1001
    }

    private var mHomeWatcherReceiver: HomeWatcherReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerHomeKeyReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterHomeKeyReceiver()
    }

    override fun onRestart() {
        super.onRestart()
        // 重新回到界面关闭服务
        textView.postDelayed({
            // 从授权界面回来, 会先调用 onActivityResult 方法，再回调 onRestart() 方法，但是我们不能把刚刚开启的服务关闭
            // 所以这里延时500ms，这时候应用已经在后台了，就不会去关闭服务了
            if (isForeground()) {
                stopService(Intent(this, WindowService::class.java))
            }
        }, 500)
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                startService(Intent(this, WindowService::class.java))
                toDesktop()
            } else {
                // TODO 有些手机点击上方的返回键不会返回回来，然后回到应用桌面点击图标会回调 onActivityResult
                //  小米8魔趣系统会出现此情况，后面可以在 onActivityResult 把桌面来的回调给拦截掉
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, REQUEST_DIALOG_PERMISSION)
            }
        } else {
            startService(Intent(this, WindowService::class.java))
            toDesktop()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_DIALOG_PERMISSION) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show()
                toDesktop()
            } else {
                startService(Intent(this, WindowService::class.java))
                toDesktop()
            }
        }
    }

    /**
     * 回到系统的桌面
     */
    private fun toDesktop() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    /**
     * 监听Home键
     */
    private fun registerHomeKeyReceiver() {
        mHomeWatcherReceiver = HomeWatcherReceiver()
        val homeFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        registerReceiver(mHomeWatcherReceiver, homeFilter)
    }

    /**
     * 取消监听Home键
     */
    private fun unRegisterHomeKeyReceiver() {
        if (null != mHomeWatcherReceiver) {
            unregisterReceiver(mHomeWatcherReceiver)
        }
    }
}