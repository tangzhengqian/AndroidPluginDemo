package com.test.demo

import android.content.Context
import dalvik.system.DexClassLoader
import java.io.File
import java.lang.reflect.InvocationTargetException

class PluginLoader {
    private var dexClassLoader: DexClassLoader? = null
    private var pluginActivity: Any? = null

    companion object {
        private var sInstance: PluginLoader? = null

        fun getInstance(): PluginLoader{
            if (sInstance == null) {
                sInstance = PluginLoader()
            }
            return sInstance!!
        }
    }

    class FuncParam {
        var method: String? = null
        var params: Array<Class<*>>? = null
        var value: Array<Any?>? = null

    }

    fun load(context: Context, pluginPath: String, className: String): Boolean {
        try {
            //            File dexOutputDir = context.getDir("dex", Context.MODE_PRIVATE);
            val dexOutputDir = File(context.externalCacheDir, "dex")
            dexClassLoader = DexClassLoader(
                pluginPath,
                dexOutputDir.absolutePath, null, context.classLoader
            )
            if (dexClassLoader != null) {
                val pluginActivityClass = dexClassLoader!!.loadClass(className)
                pluginActivity = pluginActivityClass.newInstance()
                return pluginActivity != null
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        }

        return false
    }

    fun startFunc(param: FuncParam): Boolean {
        try {
            if (dexClassLoader != null) {
                val mPluginMethod = pluginActivity!!.javaClass.getDeclaredMethod(param.method!!, *param.params!!)
                mPluginMethod.isAccessible = true
                mPluginMethod.invoke(pluginActivity, *param.value!!)
                return true
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

        return false
    }


}