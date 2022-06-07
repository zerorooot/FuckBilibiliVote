package github.zerorooot.fuckbilibilivote
//
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

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
            "{\\\"vote_id\\\":2811365,\\\"question\\\":\\\"\\346\\210\\221\\346\\234\\211\\346\\262\\241\\346\\234\\211\\350\\256\\251\\346\\221\\251\\346\\211\\230\\350\\275\\246\\351\\273\\257\\347\\204\\266\\345\\244\\261\\350\\211\\262\\\",\\\"cnt\\\":3,\\\"options\\\":[{\\\"idx\\\":1,\\\"desc\\\":\\\"\\345\\275\\223\\347\\204\\266\\357\\274\\214\\344\\275\\240\\350\\277\\267\\344\\272\\272\\357\\274\\201\\\",\\\"cnt\\\":2},{\\\"idx\\\":2,\\\"desc\\\":\\\"heitui\\\",\\\"cnt\\\":1}],\\\"icon\\\":\\\"http://i0.hdslb.com/bfs/album/5ec559dbd4d54f8c1e76021d52eb9807de94bfb9.png\\\",\\\"my_vote\\\":0,\\\"pub_dynamic\\\":false,\\\"posX\\\":150,\\\"posY\\\":200,\\\"duration\\\":7000,\\\"shrink_icon\\\":\\\"http://i0.hdslb.com/bfs/b/2eec72efb74244eed5c2f28ce5628de4e9f9c9e8.png\\\",\\\"shrink_title\\\":\\\"\\346\\212\\225\\347\\245\\250\\\",\\\"show_status\\\":0}"
                .replace("\\\"", "\"").replace("\\", "\\\\")

        val jsonArray = JSONObject(a).getJSONArray("options")
        val s2 = jsonArray.getJSONObject(1).getString("desc")

        println(MyLog().protobufToString(s2))
    }

    @Test
    fun tt() {
        val s = "command_dms {\n" +
                "  command: \"#VOTE#\"\n" +
                "  content: \"\\346\\212\\225\\347\\245\\250\\345\\274\\271\\345\\271\\225\"\n" +
                "  ctime: \"2022-06-06 17:51:38\"\n" +
                "  extra: \"{\\\"vote_id\\\":2811365,\\\"question\\\":\\\"\\346\\210\\221\\346\\234\\211\\346\\262\\241\\346\\234\\211\\350\\256\\251\\346\\221\\251\\346\\211\\230\\350\\275\\246\\351\\273\\257\\347\\204\\266\\345\\244\\261\\350\\211\\262\\\",\\\"cnt\\\":3,\\\"options\\\":[{\\\"idx\\\":1,\\\"desc\\\":\\\"\\345\\275\\223\\347\\204\\266\\357\\274\\214\\344\\275\\240\\350\\277\\267\\344\\272\\272\\357\\274\\201\\\",\\\"cnt\\\":2},{\\\"idx\\\":2,\\\"desc\\\":\\\"heitui\\\",\\\"cnt\\\":1}],\\\"icon\\\":\\\"http://i0.hdslb.com/bfs/album/5ec559dbd4d54f8c1e76021d52eb9807de94bfb9.png\\\",\\\"my_vote\\\":0,\\\"pub_dynamic\\\":false,\\\"posX\\\":150,\\\"posY\\\":200,\\\"duration\\\":7000,\\\"shrink_icon\\\":\\\"http://i0.hdslb.com/bfs/b/2eec72efb74244eed5c2f28ce5628de4e9f9c9e8.png\\\",\\\"shrink_title\\\":\\\"\\346\\212\\225\\347\\245\\250\\\",\\\"show_status\\\":0}\"\n" +
                "  id: 1068858804008963072\n" +
                "  id_str: \"1068858804008963072\"\n" +
                "  mid: 13984201\n" +
                "  mtime: \"2022-06-06 18:31:21\"\n" +
                "  oid: 739422420\n" +
                "  progress: 22755\n" +
                "}"
        println(MyLog().getVoteJsonArray(s))

    }

    private fun getPara(text: String, para: String, default: Any): String {
        text.split("\n").forEach { s ->
            if (s.contains(para)) {
                return s.replace("$para:", "")
            }
        }
        return default.toString()
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
}