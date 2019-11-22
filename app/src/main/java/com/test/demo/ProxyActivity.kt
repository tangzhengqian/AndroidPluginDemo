package com.test.demo

import android.app.Activity
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Bundle
import android.text.TextUtils
import java.lang.reflect.InvocationTargetException

/**
 * 代理Activity，插件依赖它的生命周期
 */
class ProxyActivity : Activity() {
    companion object {
        const val PLUGIN_DEX_PATH = "plugin.dex.path"
        const val PLUGIN_ACTIVITY_CLASS_NAME = "plugin.activity.class.name"
    }

    private var mPluginResources: Resources? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        if (intent != null) {
            val pluginDexPath = intent.getStringExtra(PLUGIN_DEX_PATH)
            val pluginActivityClassName = intent.getStringExtra(PLUGIN_ACTIVITY_CLASS_NAME)

            if (TextUtils.isEmpty(pluginDexPath) || TextUtils.isEmpty(pluginActivityClassName)) {
                return
            }

            loadApkResources(pluginDexPath)

            if (PluginLoader.getInstance().load(this, pluginDexPath, pluginActivityClassName)) {
                val param = PluginLoader.FuncParam()
                param.method = "setProxyActivity"
                param.params = arrayOf(Activity::class.java)
                param.value = arrayOf(this@ProxyActivity)
                PluginLoader.getInstance().startFunc(param)

                val param2 = PluginLoader.FuncParam()
                param2.method = "create"
                param2.params = arrayOf<Class<*>>(Bundle::class.java)
                param2.value = arrayOf<Any?>(savedInstanceState)
                PluginLoader.getInstance().startFunc(param2)
            }
        }
    }

    // 加载插件Apk的资源
    private fun loadApkResources(pluginDexPath: String) {
        try {
            val assetManager = AssetManager::class.java.newInstance() // 通过反射创建一个AssetManager对象
            val addAssetPathMethod = AssetManager::class.java.getDeclaredMethod(
                "addAssetPath",
                String::class.java
            ) // 获得AssetManager对象的addAssetPath方法
            addAssetPathMethod.invoke(
                assetManager,
                pluginDexPath
            ) // 调用AssetManager的addAssetPath方法，将apk的资源添加到AssetManager中管理
            mPluginResources = Resources(
                assetManager,
                super.getResources().displayMetrics,
                super.getResources().configuration
            ) // 根据AssetMananger创建一个Resources对象
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

    }

    // 重写ProxyActivity的getResources方法，让其返回插件Apk的资源对象
    override fun getResources(): Resources {
        return if (mPluginResources != null) {
            mPluginResources!!
        } else {
            super.getResources()
        }
    }
}