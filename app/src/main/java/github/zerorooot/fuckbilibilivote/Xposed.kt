package github.zerorooot.fuckbilibilivote


import android.R.attr.classLoader
import android.os.Environment
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.io.File
import java.lang.RuntimeException


class Xposed : IXposedHookLoadPackage {
    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (!lpparam.packageName.contains("bili")) {
            return
        }

//        val clazzs = "com.bapis.bilibili.app.view.v1.CommandDm\n" +
//                "com.bapis.bilibili.broadcast.message.main.CommandDm\n" +
//                "com.bapis.bilibili.community.service.dm.v1.DmViewReply\n" +
//                "com.bapis.bilibili.community.service.dm.v1.CommandDm\n" +
//                "com.bapis.bilibili.tv.interfaces.dm.v1.CommandDm\n" +
//                "com.bapis.bilibili.tv.interfaces.dm.v1.CommandDmOtt\n" +
//                "com.bilibili.lib.p2p.ControlCommandRequestResponse"
//        val split = clazzs.split("\n")
//        split.forEach {
//            XposedHelpers.findAndHookMethod(it,
//                lpparam.classLoader,
//                "getCommand",
//                object : XC_MethodHook() {
//                    override fun afterHookedMethod(param: MethodHookParam) {
//                        XposedBridge.log(RuntimeException("lsp $it ${param.result}"))
//                    }
//                })
//        }

        XposedHelpers.findAndHookMethod(
            "com.bapis.bilibili.community.service.dm.v1.DmViewReply",
            lpparam.classLoader,
            "getCommand",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val config =
                        Environment.getExternalStorageDirectory().toString() + "/Android/up"
                    val command = param.result
                    var commandDmsList: MutableList<*>? = null

                    val log = if (!File(config).exists()) {
                        "fuckbilibilivote : 清除所有弹窗"
                    } else {
                        commandDmsList =
                            (XposedHelpers.callMethod(
                                command,
                                "getCommandDmsList"
                            ) as List<*>).toMutableList()

                        "fuckbilibilivote : 显示up主弹幕"
                    }

                    XposedBridge.log("$log $command")
                    XposedHelpers.callMethod(command, "clearCommandDms")
                    if (commandDmsList != null && commandDmsList.isNotEmpty()) {
                        commandDmsList.removeIf { i ->
                            !XposedHelpers.callMethod(i, "getCommand").toString()
                                .contains("UP")
                        }
                        commandDmsList.forEach { i ->
                            XposedHelpers.callMethod(command, "addCommandDms", i)
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



