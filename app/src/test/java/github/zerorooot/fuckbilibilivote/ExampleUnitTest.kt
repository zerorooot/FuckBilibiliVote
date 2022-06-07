package github.zerorooot.fuckbilibilivote
//
import org.json.JSONObject
import org.junit.Test
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

//
///**
// * Example local unit test, which will execute on the development machine (host).
// *
// * See [testing documentation](http://d.android.com/tools/testing).
// */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val a =
            "{\\\"vote_id\\\":2811365,\\\"question\\\":\\\"\\346\\210\\221\\346\\234\\211\\346\\262\\241\\346\\234\\211\\350\\256\\251\\346\\221\\251\\346\\211\\230\\350\\275\\246\\351\\273\\257\\347\\204\\266\\345\\244\\261\\350\\211\\262\\\",\\\"cnt\\\":3,\\\"options\\\":[{\\\"idx\\\":1,\\\"desc\\\":\\\"3\\350\\277\\236\\345\\256\\211\\346\\205\\260\\344\\270\\213\\\",\\\"cnt\\\":2},{\\\"idx\\\":2,\\\"desc\\\":\\\"heitui\\\",\\\"cnt\\\":1}],\\\"icon\\\":\\\"http://i0.hdslb.com/bfs/album/5ec559dbd4d54f8c1e76021d52eb9807de94bfb9.png\\\",\\\"my_vote\\\":0,\\\"pub_dynamic\\\":false,\\\"posX\\\":150,\\\"posY\\\":200,\\\"duration\\\":7000,\\\"shrink_icon\\\":\\\"http://i0.hdslb.com/bfs/b/2eec72efb74244eed5c2f28ce5628de4e9f9c9e8.png\\\",\\\"shrink_title\\\":\\\"\\346\\212\\225\\347\\245\\250\\\",\\\"show_status\\\":0}"
                .replace("\\\"", "\"").replace("\\", "\\\\")

        val jsonArray = JSONObject(a).getJSONArray("options")
        val s1 = jsonArray.getJSONObject(0).getString("desc")
        val s2=jsonArray.getJSONObject(1).getString("desc")
        println(MyLog().protobufToString(s2)+"\n"+MyLog().protobufToString(s1))
    }

    @Test
    fun tt() {
        val char = "108"
        println(URLEncoder.encode(char, StandardCharsets.UTF_8.name()))
    }

    private fun getPara(text: String, para: String, default: Any): String {
        text.split("\n").forEach { s ->
            if (s.contains(para)) {
                return s.replace("$para:", "")
            }
        }
        return default.toString()
    }

    @Test
     fun protobufToString() {
        val str = URLEncoder.encode("9连安慰下", "UTF-8")
        val s = str.substring(1).split("%".toRegex()).toTypedArray()
        val s8 = StringBuffer()
        for (i in s.indices) {
            s8.append("\\" + Integer.toOctalString(Integer.valueOf(s[i], 16)))
        }
        println(s8.toString())
    }
}