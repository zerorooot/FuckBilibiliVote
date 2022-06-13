package github.zerorooot.fuckbilibilivote

import org.json.JSONArray
import org.json.JSONObject
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

class MyLog {
    fun getLog(paramString: String): String {
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

        return print.toString()
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
        val time = if (entTime != 0) {
            "${getTime(startTime)} ~ ${getTime(entTime)}"
        } else {
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

    fun protobufToString(text: String): String {
        val split: List<String> = text
            .replace(" ", "\\40\\").replace("!", "\\41\\").replace("#", "\\43\\")
            .replace("$", "\\44\\").replace("%", "\\45\\").replace("&", "\\46\\")
            .replace("'", "\\47\\").replace("(", "\\50\\").replace(")", "\\51\\")
            .replace("*", "\\52\\").replace("+", "\\53\\").replace(",", "\\54\\")
            .replace(".", "\\56\\").replace("/", "\\57\\").replace(":", "\\72\\")
            .replace(";", "\\73\\").replace("=", "\\75\\").replace("?", "\\77\\")
            .replace("@", "\\100\\").replace("[", "\\133\\").replace("/", "\\134\\")
            .replace("]", "\\135\\")
            .trim().split("\\")
        val s16 = StringBuilder()
        for (s in split) {
            if (s != "") {
                val number = Pattern.compile("\\D").matcher(s).replaceAll("").trim()
                //全部英文
                // heitui
                if (number == "") {
                    val charURLEncoder = URLEncoder.encode(s, StandardCharsets.UTF_8.name())
                    s16.append(charURLEncoder)
                    continue
                }

                val numberURLEncoder = try {
                    "%${Integer.toHexString(Integer.valueOf(number, 8))}"
                }catch (e:Exception){
                    "%20"
                }
                //数字
                if (numberURLEncoder == "%$number") {
                    s16.append(number.replace("%", ""))
                    continue
                }
                //全部中文
                if (number == s) {
                    s16.append(numberURLEncoder)
                    continue
                }
                //全部中文、英文混合
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

}


