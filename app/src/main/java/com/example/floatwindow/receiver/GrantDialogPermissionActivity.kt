package com.example.floatwindow.receiver

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import com.example.floatwindow.service.WindowService

class GrantDialogPermissionActivity : Activity() {

    companion object {
        private const val REQUEST_DIALOG_PERMISSION = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_DIALOG_PERMISSION) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show()
            } else {
                startService(Intent(this, WindowService::class.java))
            }
            finish()
        }
    }

    private fun requestPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        startActivityForResult(intent,
            REQUEST_DIALOG_PERMISSION
        )
    }
}