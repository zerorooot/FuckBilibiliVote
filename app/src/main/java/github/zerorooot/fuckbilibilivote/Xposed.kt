package github.zerorooot.fuckbilibilivote


import android.R.attr.classLoader
import android.os.Environment
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
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
        val config =
            Environment.getExternalStorageDirectory().toString() + "/Android/up"
//
//        val clazzs = "com.bapis.bilibili.app.view.v1.CommandDm\n" +
//                "com.bapis.bilibili.broadcast.message.main.CommandDm\n" +
//                "com.bapis.bilibili.community.service.dm.v1.DmViewReply\n" +
//                "com.bapis.bilibili.community.service.dm.v1.CommandDm\n" +
//                "com.bapis.bilibili.tv.interfaces.dm.v1.CommandDm\n" +
//                "com.bapis.bilibili.tv.interfaces.dm.v1.CommandDmOtt\n" +
//                "com.bilibili.lib.p2p.ControlCommandRequestResponse"
//        val split = clazzs.split("\n")
//
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

        //新版
        newVersion(lpparam, config)

        //play版
//        playVersion(lpparam, config)

        //旧版
        oldVersion(lpparam, config)

    }

    private fun oldVersion(lpparam: LoadPackageParam, config: String) {
        val getVideoGuide = XposedHelpers.findMethodExactIfExists(
            "com.bapis.bilibili.app.view.v1.ViewProgressReply",
            lpparam.classLoader, "getVideoGuide"
        )
        if (getVideoGuide == null) {
            XposedBridge.log("not find ViewProgressReply getVideoGuide")
            return
        }
        XposedBridge.hookMethod(getVideoGuide, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                var commandDmsList: MutableList<*>? = null

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

                XposedBridge.log("$log 旧版 ${param.result}")
                XposedHelpers.callMethod(param.thisObject, "clearVideoGuide")


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
        })
    }

    private fun newVersion(lpparam: LoadPackageParam, config: String) {
        val getCommand =
            XposedHelpers.findMethodExactIfExists(
                "com.bapis.bilibili.community.service.dm.v1.DmViewReply",
                lpparam.classLoader, "getCommand"
            )

        if (getCommand == null) {
            XposedBridge.log("fuckbilibilivote : not find DmViewReply getCommand")
            return
        }


        XposedBridge.hookMethod(getCommand, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val command = param.result
                var commandDmsLists: MutableList<*>? = null

                val log = if (!File(config).exists()) {
                    "fuckbilibilivote : 清除所有弹窗"
                } else {
                    commandDmsLists =
                        (XposedHelpers.callMethod(
                            command,
                            "getCommandDmsList"
                        ) as List<*>).toMutableList()

                    "fuckbilibilivote : 显示up主弹幕"
                }

                XposedBridge.log("$log 新版 $command")
                XposedHelpers.callMethod(command, "clearCommandDms")
                if (commandDmsLists != null && commandDmsLists.isNotEmpty()) {
                    commandDmsLists.removeIf { i ->
                        !XposedHelpers.callMethod(i, "getCommand").toString()
                            .contains("UP")
                    }
                    commandDmsLists.forEach { i ->
                        XposedHelpers.callMethod(command, "addCommandDms", i)
                    }

                }


            }
        })

    }

    private fun playVersion(lpparam: LoadPackageParam, config: String) {
        val setCommandDMS = XposedHelpers.findMethodExactIfExists(
            "tv.danmaku.biliplayerv2.service.interact.biz.chronos.chronosrpc.methods.send.ViewProgressChange\$VideoGuide",
            lpparam.classLoader,
            "setCommandDMS",
            MutableList::class.java,
        )

        if (setCommandDMS == null) {
            XposedBridge.log("fuckbilibilivote : not find ViewProgressChange\$VideoGuide setCommandDMS")
            return
        }

        XposedBridge.hookMethod(setCommandDMS, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val command = param.args[0] as MutableList<*>
                if (!File(config).exists()) {
                    param.args[0] = emptyList<Any>()
                    XposedBridge.log("fuckbilibilivote : 清除所有弹窗 play version $command")
                    XposedBridge.log("fuckbilibilivote : 清除所有弹窗 play args ${param.args[0]}")
                } else {
                    command.removeIf { i ->
                        !XposedHelpers.callMethod(i, "getCommand").toString()
                            .contains("UP")
                    }
                    param.args[0] = command
                    XposedBridge.log("fuckbilibilivote : 显示up主弹幕 play version $command")
                }
            }
        })

    }
}



