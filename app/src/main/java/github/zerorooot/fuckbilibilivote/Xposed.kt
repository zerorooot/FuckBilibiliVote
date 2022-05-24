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
        autoHook(lpparam)

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
                XposedBridge.log("auto hook bili vote success ${param.result}")
                param.result = null
            }
        })
    }


}