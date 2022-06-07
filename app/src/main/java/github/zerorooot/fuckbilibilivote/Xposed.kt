package github.zerorooot.fuckbilibilivote

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam


class Xposed : IXposedHookLoadPackage {


    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (!lpparam.packageName.contains("bili")) {
            return
        }

        val viewProgressReplyClass = XposedHelpers.findClass(
            "com.bapis.bilibili.app.view.v1.ViewProgressReply",
            lpparam.classLoader
        )
        val method = viewProgressReplyClass.getDeclaredMethod("getVideoGuide")
        method.isAccessible = true
        XposedBridge.hookMethod(method, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val string = param.result.toString()
                if (string.contains("command_dms")) {
                    param.result = null
                    try {
                        MyLog().printLog(string)
                    } catch (e: Exception) {
                        XposedBridge.log(e)
                        XposedBridge.log(string)
                    }
                }

            }
        })

    }
}


