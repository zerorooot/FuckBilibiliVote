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
                    var commandDmsList: MutableList<*>? = null

                    val config =
                        Environment.getExternalStorageDirectory().toString() + "/Android/up"
                    val log = if (!File(config).exists()) {
                        "fuckbilibilivote : 清除所有弹窗"
                    } else {
                        commandDmsList =
                            (XposedHelpers.callMethod(
                                param.result,
                                "getCommandDmsList"
                            ) as List<*>).toMutableList()

                        "fuckbilibilivote : 显示up主弹幕"
                    }

                    XposedHelpers.callMethod(param.thisObject, "clearVideoGuide")
                    XposedBridge.log(log)

                    if (commandDmsList != null && commandDmsList.isNotEmpty()) {
                        commandDmsList.removeIf { i ->
                            !XposedHelpers.callMethod(i, "getCommand").toString()
                                .contains("UP")
                        }
                        commandDmsList.forEach { i ->
                            XposedHelpers.callMethod(param.thisObject, "addCommandDms", i)
                        }

                    }


                }
            }
        )
        //1.7万+人同时在看~
        //DanmakuEventBus subscribeUniversalWidgtsMsgs OnOnlineInfoChanged {"args":{"icon_url":"","show_special":false,"special_content":"1.6万+人同时在看~","video_id":"884337754","viewer_content":"1.6万+人正在看","work_id":"859990221"}}

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



