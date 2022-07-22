package github.zerorooot.fuckbilibilivote

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import org.json.JSONArray
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
                        param.result = null
                        print.put("command_dms", myLog.getDmsLog(result))
                    }

                    if (printInfo != print.toString()) {
                        XposedBridge.log(print.toString())
                        printInfo = print.toString()
                    }

                }
            })
    }
}


