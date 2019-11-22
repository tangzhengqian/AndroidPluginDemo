package com.test.plugin

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log

class MainActivity : Activity() {
    private var mProxyActivity: Activity? = null

    fun setProxyActivity(proxyActivity: Activity) {
        Log.e("tzq", "setProxyActivity proxyActivity=$proxyActivity")
        mProxyActivity = proxyActivity
    }

    fun create(savedInstanceState: Bundle?) {
        Log.e("tzq", "plugin MainActivity onCreate")
        Log.e(
            "tzq",
            "plugin MainActivity onCreate, mProxyActivity=$mProxyActivity  savedInstanceState=$savedInstanceState"
        )
        mProxyActivity?.setContentView(R.layout.activity_main)
    }

    fun exec(context: Context) {
        Log.e("tzq", "now exec.... context=$context")
    }
}