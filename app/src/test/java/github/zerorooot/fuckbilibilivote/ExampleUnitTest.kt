package github.zerorooot.fuckbilibilivote

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val allJson = JSONObject()

        val biliJson1 = JSONObject()
        val biliJson2 = JSONObject()
        val biliJson3 = JSONObject()

        biliJson1.put("version", "6.73.1")
        biliJson1.put(
            "className",
            "tv.danmaku.chronos.wrapper.chronosrpc.remote.RemoteServiceHandler"
        )
        biliJson1.put("methodName", "m0")

        biliJson2.put("version", "6.15.1")
        biliJson2.put("className", "tv.danmaku.chronos.wrapper.ChronosService")
        biliJson2.put("methodName", "J6")

        biliJson3.put("version", "customize")
        biliJson3.put("className", "")
        biliJson3.put("methodName", "")

        val biliJsonArray = JSONArray()
        biliJsonArray.put(biliJson1)
        biliJsonArray.put(biliJson2)
        biliJsonArray.put(biliJson3)

        val hdJson1 = JSONObject()
        hdJson1.put("version", "1.19.0")
        hdJson1.put("className", "tv.danmaku.chronos.wrapper.rpc.remote.RemoteServiceHandler")
        hdJson1.put("methodName", "g0")

        val hdJsonArray = JSONArray()
        hdJsonArray.put(hdJson1)
        hdJsonArray.put(biliJson3)
        hdJsonArray.put(biliJsonArray)

        allJson.put("biliJson", biliJsonArray)
        allJson.put("hdJson", hdJsonArray)

        println(allJson.toString())


//        println(array.toString())

    }
}