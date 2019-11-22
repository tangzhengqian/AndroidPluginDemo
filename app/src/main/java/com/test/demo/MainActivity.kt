package com.test.demo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        // 检查权限状态
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //  用户彻底拒绝授予权限，一般会提示用户进入设置权限界面
        } else {
            //  用户未彻底拒绝授予权限
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }
        return false
    }

    fun clickBt1(view: View) {
        Log.e("tzq", "clickBt1")
        if (!checkPermission()) {
            return
        }
        val intent = Intent()
        intent.putExtra(ProxyActivity.PLUGIN_DEX_PATH, "/sdcard/plugin-debug.apk")
        intent.putExtra(ProxyActivity.PLUGIN_ACTIVITY_CLASS_NAME, "com.test.plugin.MainActivity")
        intent.setClass(this@MainActivity, ProxyActivity::class.java)
        startActivity(intent)
    }

    fun clickBt2(view: View) {
        Log.e("tzq", "clickBt2---")
        if (!checkPermission()) {
            return
        }
        if (PluginLoader.getInstance().load(
                this@MainActivity,
                "/sdcard/plugin-debug.apk",
                "com.test.plugin.MainActivity"
            )
        ) {
            val param = PluginLoader.FuncParam()
            param.method = "exec"
            param.params = arrayOf(Context::class.java)
            param.value = arrayOf(this@MainActivity)

            PluginLoader.getInstance().startFunc(param)

            val param2 = PluginLoader.FuncParam()
            param2.method = "test"
            param2.params = arrayOf<Class<*>>(String.javaClass)
            param2.value = arrayOf<Any?>("hello")
            PluginLoader.getInstance().startFunc(param2)
        }
    }
}