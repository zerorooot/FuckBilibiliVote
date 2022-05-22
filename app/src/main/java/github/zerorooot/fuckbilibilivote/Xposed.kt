package github.zerorooot.fuckbilibilivote

import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class Xposed : IXposedHookLoadPackage {
    private val bili = "tv.danmaku.bili"
    private val biliHd = "tv.danmaku.bilibilihd"

    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (bili == lpparam.packageName) {
            getConfigAndHook("bili_class_name", "bili_class_method", lpparam)
        }
        if (biliHd == lpparam.packageName) {
            getConfigAndHook("bili_hd_class_name", "bili_hd_class_method", lpparam)
        }
    }

    @Throws(NoSuchMethodException::class)
    private fun getConfigAndHook(
        classNameSp: String,
        methodNameSp: String,
        lpparam: LoadPackageParam
    ) {
        val xsp = XSharedPreferences(BuildConfig.APPLICATION_ID, "bili")
        val className = xsp.getString(classNameSp, "")
        val methodName = xsp.getString(methodNameSp, "")
        XposedBridge.log("get bili class name:$className method name:$methodName")
        biliHook(className, methodName, lpparam)
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
                XposedBridge.log("fuck bili vote " + lpparam.packageName + "  " + param.args[0].toString())
                param.args[0] = null
            }
        })
    }
}