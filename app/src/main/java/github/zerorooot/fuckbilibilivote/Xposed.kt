package github.zerorooot.fuckbilibilivote

import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam


class Xposed : IXposedHookLoadPackage {
    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (!lpparam.packageName.contains("bili")) {
            return
        }
        XposedBridge.log("hook ${lpparam.packageName}")
        val xsp = XSharedPreferences(BuildConfig.APPLICATION_ID, "bili")
        val isAutoHook = xsp.getBoolean("auto_hook", true)
        if (isAutoHook) {
            autoHook(lpparam)
            return
        }
        val className = xsp.getString("bili_class_name", "")
        val methodName = xsp.getString("bili_class_method", "")
        biliHook(className, methodName, lpparam)
    }


    private fun autoHook(lpparam: LoadPackageParam) {
        val viewProgressReplyClass = XposedHelpers.findClass(
            "com.bapis.bilibili.app.view.v1.ViewProgressReply",
            lpparam.classLoader
        )
        val method = viewProgressReplyClass.getDeclaredMethod("getVideoGuide")
        method.isAccessible = true
        XposedBridge.hookMethod(method, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                XposedBridge.log("auto hook bili vote success")
                param.result = null
            }
        })
    }

    @Throws(NoSuchMethodException::class)
    private fun biliHook(className: String?, methodName: String?, lpparam: LoadPackageParam) {
        val hookClass = XposedHelpers.findClass(className, lpparam.classLoader)
        val viewProgressReplyClass = XposedHelpers.findClass(
            "com.bapis.bilibili.app.view.v1.ViewProgressReply",
            lpparam.classLoader
        )
        val method = hookClass.getDeclaredMethod(
            methodName, viewProgressReplyClass,
            Long::class.javaPrimitiveType,
            Long::class.javaPrimitiveType
        )
        method.isAccessible = true
        XposedBridge.hookMethod(method, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                XposedBridge.log("hook bili vote success")
                param.args[0] = null
            }
        })
    }
}