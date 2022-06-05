package github.zerorooot.fuckbilibilivote

import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLDecoder
import java.net.URLEncoder
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
                    getProgress(s)
                )
            }
            if (s.contains("LINK")) {
                print.put("link", getLinkJsonArray(s))
            }
            if (s.contains("VOTE")) {
                print.put("vote", getVoteJsonArray(s))
            }
            if (s.contains("UP")) {
                print.put("up", getUpJsonArray(s, print))
            }
            if (s.contains("GRADE")) {
                val msg = protobufToString(getExtra(s).getString("msg"))
                print.put(
                    "grade",
                    getProgress(s).put("msg", msg)
                )
            }
        }
        if (printInfo != print.toString()) {
            XposedBridge.log(print.toString())
            printInfo = print.toString()
        }
    }

    private fun getUpJsonArray(text: String, print: JSONObject): JSONArray {
        val content = protobufToString(getPara(text, "content", "").replace("\"", ""))
        val jsonArray = if (print.has("up")) {
            print.getJSONArray("up")
        } else {
            JSONArray()
        }
        jsonArray.put(
            JSONObject().put(
                "content",
                content
            ).put("time", getTime(getPara(text, "progress", 0).toInt()))
        )

        return jsonArray
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
            getProgress(s).put("question", question)
                .put("option1", option1).put("option2", option2)
        )

    }

    private fun getLinkJsonArray(s: String): JSONArray {
        val title = protobufToString(getExtra(s).getString("title"))
        val content = protobufToString(getPara(s, "content", "").replace("\"", ""))
        val aid = getExtra(s).getString("aid")
        val bvid = getExtra(s).getString("bvid")
        val jsonArray = JSONArray()
        return jsonArray.put(
            getProgress(s)
                .put("content", content)
                .put("title", title)
                .put("aid", aid)
                .put("bvid", bvid)
        )
    }

    private fun getProgress(text: String): JSONObject {
        val startTime = getPara(text, "progress", 0).toInt()
        val entTime = try {
            val duration = getExtra(text).getInt("duration")
            startTime + duration
        } catch (e: Exception) {
            0
        }
        val time=if(entTime!=0){
            "${getTime(startTime)} ~ ${getTime(entTime)}"
        }else{
            getTime(startTime)
        }
        return JSONObject().put("time", time)
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
    val split: List<String> = text.trim().split("\\")
    val s16 = StringBuilder()
    for (s in split) {
        if (s != "") {
            val number = Pattern.compile("\\D").matcher(s).replaceAll("").trim()
            val numberURLEncoder = "%${Integer.toHexString(Integer.valueOf(number, 8))}"

            if (number == s) {
                s16.append(numberURLEncoder)
                continue
            }

            // \\242MIAN\\244| \\275~
            val char = s.replace(number, "")
            val charURLEncoder = URLEncoder.encode(char, StandardCharsets.UTF_8.name())

            val append = if (s.startsWith(char)) {
                charURLEncoder + numberURLEncoder
            } else {
                numberURLEncoder + charURLEncoder
            }

            s16.append(append)
        }
    }
    return URLDecoder.decode(s16.toString(), StandardCharsets.UTF_8.name())
}
