package com.example.floatwindow.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import com.example.floatwindow.MainActivity
import com.example.floatwindow.R
import com.example.floatwindow.helper.DensityUtils

class WindowService : Service() {

    private var params: WindowManager.LayoutParams? = null
    private var customView: View? = null // 自定义的悬浮框
    private var windowManager: WindowManager? = null

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        customView = LayoutInflater.from(this).inflate(R.layout.float_window, null)
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createView()
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createView() {
        params = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            0,
            0,
            PixelFormat.TRANSPARENT
        )
        params?.apply {
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            type =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.LAST_SYSTEM_WINDOW
            gravity = Gravity.TOP or Gravity.START
            width = DensityUtils.dp2px(this@WindowService, 60F)
            height = DensityUtils.dp2px(this@WindowService, 55F)
            x = DensityUtils.dp2px(this@WindowService, 10F)
            y = DensityUtils.dp2px(this@WindowService, 100F)
        }
        windowManager?.addView(customView, params)

        customView?.setOnTouchListener { _, event ->
            val rawX = event.rawX.toInt()
            val rawY = event.rawY.toInt()
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    params?.x = rawX - DensityUtils.dp2px(this@WindowService, 30F)
                    params?.y = rawY - DensityUtils.dp2px(this@WindowService, 45F)
                    windowManager?.updateViewLayout(customView, params)
                }
            }
            return@setOnTouchListener false
        }
        customView?.setOnClickListener {
            val intent = Intent(this@WindowService, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager?.removeView(customView)
    }
}