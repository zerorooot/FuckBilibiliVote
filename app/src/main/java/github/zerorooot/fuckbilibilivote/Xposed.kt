package github.zerorooot.fuckbilibilivote

import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern


class Xposed : IXposedHookLoadPackage {
    private var printInfo = ""

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
                        printLog(string)
                    } catch (e: Exception) {
                        XposedBridge.log(e)
                        XposedBridge.log(string)
                    }
                }

            }
        })

    }

    private fun printLog(paramString: String) {
        val list = ArrayList<String>()
        val print = JSONObject()
        paramString.replace(" ", "").split("command_dms").forEach { s ->
            if (s.contains("command")) {
                list.add(s)
            }
        }
        list.forEach { s ->
            if (s.contains("ATTENTION")) {
                print.put(
                    "attention",
                    getContentAndProgress(s)
                )
            }
            if (s.contains("LINK")) {
                print.put("link", getLinkJsonArray(s))
            }
            if (s.contains("VOTE")) {
                print.put("vote", getVoteJsonArray(s))
            }
            if (s.contains("UP")) {
                print.put(
                    "up",
                    getContentAndProgress(s)
                )
            }
            if (s.contains("GRADE")) {
                val msg = protobufToString(getExtra(s).getString("msg"))
                print.put(
                    "grade",
                    getContentAndProgress(s).put("msg", msg)
                )
            }
        }
        if (printInfo != print.toString()) {
            XposedBridge.log(print.toString())
            printInfo = print.toString()
        }
    }

    private fun getVoteJsonArray(s: String): JSONArray {
        val question = protobufToString(getExtra(s).getString("question"))
        val option1 = protobufToString(
            getExtra(s).getJSONArray("options").getJSONObject(0).getString("desc")
        )
        val option2 = protobufToString(
            getExtra(s).getJSONArray("options").getJSONObject(1).getString("desc")
        )
        val jsonArray = JSONArray()
        return jsonArray.put(
            getContentAndProgress(s).put("question", question)
                .put("option1", option1).put("option2", option2)
        )

    }

    private fun getLinkJsonArray(s: String): JSONArray {
        val title = protobufToString(getExtra(s).getString("title"))
        val aid = getExtra(s).getString("aid")
        val bvid = getExtra(s).getString("bvid")
        val jsonArray = JSONArray()
        return jsonArray.put(
            getContentAndProgress(s).put("title", title)
                .put("aid", aid)
                .put("bvid", bvid)
        )
    }

    private fun getContentAndProgress(text: String): JSONObject {
        val content = protobufToString(getPara(text, "content", ""))
        val startTime = getPara(text, "progress", 0).toInt()
        val duration = try {
            getExtra(text).getInt("duration")
        } catch (e: Exception) {
            0
        }
        val entTime = startTime + duration
        return JSONObject().put("content", content)
            .put("time", "${getTime(startTime)} ~ ${getTime(entTime)}")
    }

    private fun getTime(progress: Int): String {
        val time = progress / 1000
        val m = (time % 3600 / 60).toString().padStart(2, '0')
        val ss = (time % 3600 % 60).toString().padStart(2, '0')
        return "$m:$ss"
    }

    private fun getExtra(text: String): JSONObject {
        val extra =
            getPara(text, "extra", "\"{}\"")
                .subSequence(1, getPara(text, "extra", "\"{}\"").length - 1)
                .toString()
                .replace("\\\"", "\"").replace("\\", "\\\\")
        return JSONObject(extra)
    }

    private fun getPara(text: String, para: String, default: Any): String {
        text.split("\n").forEach { s ->
            if (s.contains(para)) {
                return s.replace("$para:", "")
            }
        }
        return default.toString()
    }

}

private fun protobufToString(text: String): String {
    val split: List<String> =
        Pattern.compile("[^0-9|\\\\]").matcher(text).replaceAll("").trim()
            .split("\\")
    val s16 = StringBuilder()
    for (s in split) {
        if (s != "") {
            s16.append("%").append(Integer.toHexString(Integer.valueOf(s, 8)))
        }
    }
    return URLDecoder.decode(s16.toString(), StandardCharsets.UTF_8.name())
}
