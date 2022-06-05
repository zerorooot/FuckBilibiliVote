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
            "\\346\\250\\252\\347\\211\\210\\346\\234\\211\\346\\203\\212\\345\\226\\234\\345\\223\\246\\357\\275\\236"
//        val b="\\242MIAN\\244| \\275~"
        println(protobufToString(a))

    }
    @Test
    fun tt() {
        val s="command_dms {\n" +
                "  command: \"#LINK#\"\n" +
                "  content: \"\\346\\250\\252\\347\\211\\210\\346\\234\\211\\346\\203\\212\\345\\226\\234\\345\\223\\246\\357\\275\\236\"\n" +
                "  ctime: \"2022-06-05 17:49:08\"\n" +
                "  extra: \"{\\\"aid\\\":299515211,\\\"title\\\":\\\"\\343\\200\\220\\351\\235\\242MIAN\\343\\200\\221\\346\\201\\213\\347\\210\\261\\345\\221\\212\\346\\200\\245\\342\\235\\244| \\346\\214\\207\\347\\274\\235\\344\\270\\255\\351\\235\\222\\346\\266\\251\\350\\220\\214\\350\\212\\275~\\344\\270\\200\\347\\234\\274\\345\\210\\260\\350\\200\\201\\\",\\\"icon\\\":\\\"http://i0.hdslb.com/bfs/archive/03ef3f34944e0f78b1b4050fc3f9705d1fa905e3.png\\\",\\\"bvid\\\":\\\"BV1YF41157xd\\\",\\\"arc_pic\\\":\\\"http://i2.hdslb.com/bfs/archive/e5577bca67c9e1599d0a4f62777a7ffb83ed1919.jpg\\\",\\\"arc_duration\\\":90,\\\"shrink_icon\\\":\\\"http://i0.hdslb.com/bfs/b/44338bca6bb98a34da40698beb4ee7d19aea92a6.png\\\",\\\"shrink_title\\\":\\\"\\350\\247\\206\\351\\242\\221\\\",\\\"show_status\\\":0,\\\"duration\\\":5000,\\\"arc_type\\\":0}\"\n" +
                "  id: 1068132771135750912\n" +
                "  id_str: \"1068132771135750912\"\n" +
                "  mid: 1633788818\n" +
                "  mtime: \"2022-06-05 17:49:08\"\n" +
                "  oid: 738123298\n" +
                "  progress: 1259\n" +
                "}"
        println(getPara(s, "content", ""))
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

                val append = if(s.startsWith(char)){
                    charURLEncoder + numberURLEncoder
                }else{
                    numberURLEncoder + charURLEncoder
                }

                s16.append(append)
            }
        }
        return URLDecoder.decode(s16.toString(), StandardCharsets.UTF_8.name())
    }
}