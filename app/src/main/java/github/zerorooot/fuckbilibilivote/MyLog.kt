package github.zerorooot.fuckbilibilivote

import de.robv.android.xposed.XposedHelpers
import org.json.JSONArray
import org.json.JSONObject

class MyLog() {
    fun getOptionCardLog(obj: Any): JSONArray {
        val operationCardList = XposedHelpers.callMethod(obj, "getOperationCardNewList") as List<*>
        val print = JSONArray()
        operationCardList.forEach {
            val standardJson = JSONObject()
            val title =
                XposedHelpers.callMethod(XposedHelpers.callMethod(it, "getStandard"), "getTitle")
            val from = XposedHelpers.callMethod(it, "getFrom")
            val to = XposedHelpers.callMethod(it, "getTo")
            standardJson.put("title", title)
            standardJson.put("time", "$from ~ $to")
            print.put(standardJson)
        }

        return print
    }

    fun getDmsLog(obj: Any): JSONObject {
        val commandDmsList =
            XposedHelpers.callMethod(obj, "getCommandDmsList") as List<*>

        val print = JSONObject()

        commandDmsList.forEach {
            val commandDmsUtil = CommandDmsUtil(it!!)
            val command = commandDmsUtil.getCommand()
            val content = commandDmsUtil.getContent()
            val extra = commandDmsUtil.getExtra()
            val progress = commandDmsUtil.getProgress()
            when (command) {
                "ATTENTION" -> {
                    print.put(
                        "attention",
                        getProgress(progress, extra)
                    )
                }
                "LINK" -> {
                    print.put("link", getLinkJsonArray(extra, content, progress))
                }
                "VOTE" -> {
                    print.put("vote", getVoteJsonArray(extra, progress))
                }
                "UP" -> {
                    print.put("up", getUpJsonArray(print, content, progress))
                }
                "GRADE" -> {
                    val msg = extra.getString("msg")
                    print.put(
                        "grade",
                        getProgress(progress, extra).put("msg", msg)
                    )
                }
            }

        }


        return print
    }

    private fun getUpJsonArray(print: JSONObject, content: String, progress: Int): JSONArray {
        val jsonArray = if (print.has("up")) {
            print.getJSONArray("up")
        } else {
            JSONArray()
        }
        jsonArray.put(
            JSONObject().put(
                "content",
                content
            ).put("time", getTime(progress))
        )

        return jsonArray
    }

    private fun getVoteJsonArray(extra: JSONObject, progress: Int): JSONArray {
        val question = extra.getString("question")
        val option1 = extra.getJSONArray("options").getJSONObject(0).getString("desc")
        val option2 = extra.getJSONArray("options").getJSONObject(1).getString("desc")
        val jsonArray = JSONArray()
        return jsonArray.put(
            getProgress(progress, extra).put("question", question)
                .put("option1", option1).put("option2", option2)
        )

    }

    private fun getLinkJsonArray(extra: JSONObject, content: String, progress: Int): JSONArray {
        val title = extra.getString("title")
        val aid = extra.getString("aid")
        val bvid = extra.getString("bvid")
        val jsonArray = JSONArray()
        return jsonArray.put(
            getProgress(progress, extra)
                .put("content", content)
                .put("title", title)
                .put("aid", aid)
                .put("bvid", bvid)
        )
    }

    private fun getProgress(startTime: Int, extra: JSONObject): JSONObject {
        val entTime = try {
            val duration = extra.getInt("duration")
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

}


