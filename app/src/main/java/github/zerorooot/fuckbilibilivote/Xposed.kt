package github.zerorooot.fuckbilibilivote


import android.os.Environment
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.io.File


class Xposed : IXposedHookLoadPackage {
    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (!lpparam.packageName.contains("bili")) {
            return
        }


        XposedHelpers.findAndHookMethod(
            "com.bapis.bilibili.app.view.v1.ViewProgressReply",
            lpparam.classLoader, "getVideoGuide", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val config =
                        Environment.getExternalStorageDirectory().toString() + "/Android/up"
                    if (!File(config).exists()) {
                        XposedBridge.log("fuckbilibilivote : 清除所有弹窗")
                        XposedHelpers.callMethod(param.thisObject, "clearVideoGuide")
                        return
                    }

                    XposedBridge.log("fuckbilibilivote : 显示up主弹幕")
                    val result = param.result
                    val methodMap =
                        mapOf(
                            "getAttentionList" to "clearAttention",
                            "getCardsSecondList" to "clearCardsSecond",
                            "getOperationCardList" to "clearOperationCard",
                            "getOperationCardNewList" to "clearOperationCardNew"
                        )

                    methodMap.forEach { (t, u) ->
                        if ((XposedHelpers.callMethod(result, t) as List<*>).isNotEmpty()) {
                            XposedHelpers.callMethod(result, u)
                        }
                    }

                    val commandDmsList =
                        (XposedHelpers.callMethod(result, "getCommandDmsList") as List<*>).toMutableList()
                    XposedHelpers.callMethod(result, "clearCommandDms")
                    if (commandDmsList.isNotEmpty()) {
                        commandDmsList.removeIf { i ->
                            !XposedHelpers.callMethod(i, "getCommand").toString()
                                .contains("UP")
                        }
                        commandDmsList.forEach { i->
                            XposedHelpers.callMethod(result, "addCommandDms", i)
                        }

                    }


                }
            }
        )

        //番剧出现的广告，https://b23.tv/ep508404，21:52秒左右
        //com.bilibili.bangumi.remote.http.server.RemoteLogicService getOperationCardList
        //6.85.0
//        XposedHelpers.findAndHookMethod("retrofit2.Retrofit",
//            lpparam.classLoader,
//            "loadServiceMethod",
//            Method::class.java,
//            object : XC_MethodHook() {
//                @Throws(Throwable::class)
//                override fun beforeHookedMethod(param: MethodHookParam) {
//                    val method = param.args[0] as Method
//                    XposedBridge.log("msj ${method.name}")
//                    if (method.name == "getOperationCardList") {
//                        param.result = null
//                    }
//                }
//            })
    }

}



