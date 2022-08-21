package github.zerorooot.fuckbilibilivote

import android.R.attr.classLoader
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import org.json.JSONObject


class Xposed : IXposedHookLoadPackage {
    private val myLog by lazy { MyLog() }
    private var printInfo = ""

    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (!lpparam.packageName.contains("bili")) {
            return
        }


        XposedHelpers.findAndHookMethod("com.bapis.bilibili.app.view.v1.ViewProgressReply",
            lpparam.classLoader, "getVideoGuide", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val result = param.result

                    val print = JSONObject()

                    if (result.toString().contains("operation_card_new")) {
                        print.put("operation_card_new", myLog.getOptionCardLog(result))

                        XposedHelpers.callMethod(result, "clearOperationCard")
                        XposedHelpers.callMethod(result, "clearOperationCardNew")
                    }

                    if (result.toString().contains("command_dms")) {
                        XposedHelpers.callMethod(param.thisObject, "clearVideoGuide")
                        print.put("command_dms", myLog.getDmsLog(result))
                    }

                    if (print.length() != 0 && printInfo != print.toString()) {
                        XposedBridge.log(print.toString())
                        printInfo = print.toString()
                    }

                }
            })

//番剧出现的广告，https://b23.tv/ep508404，21:52秒左右
        //com.bilibili.bangumi.remote.http.server.RemoteLogicService getOperationCardList
        //6.85.0
        XposedHelpers.findAndHookMethod("com.bilibili.bangumi.remote.http.impl.f",
            lpparam.classLoader,
            "q",
            Long::class.javaPrimitiveType,
            Int::class.javaPrimitiveType,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                        param.result = null
                }
            })

    }
}


