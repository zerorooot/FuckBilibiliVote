package github.zerorooot.fuckbilibilivote
//
import org.json.JSONObject
import org.junit.Test
import java.net.URLDecoder
import java.net.URLEncoder
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
            "{\\\"aid\\\":299515211,\\\"title\\\":\\\"\\343\\200\\220\\351\\235\\242MIAN\\343\\200\\221\\346\\201\\213\\347\\210\\261\\345\\221\\212\\346\\200\\245\\342\\235\\244| \\346\\214\\207\\347\\274\\235\\344\\270\\255\\351\\235\\222\\346\\266\\251\\350\\220\\214\\350\\212\\275~\\344\\270\\200\\347\\234\\274\\345\\210\\260\\350\\200\\201\\\",\\\"icon\\\":\\\"http://i0.hdslb.com/bfs/archive/03ef3f34944e0f78b1b4050fc3f9705d1fa905e3.png\\\",\\\"bvid\\\":\\\"BV1YF41157xd\\\",\\\"arc_pic\\\":\\\"http://i2.hdslb.com/bfs/archive/e5577bca67c9e1599d0a4f62777a7ffb83ed1919.jpg\\\",\\\"arc_duration\\\":90,\\\"shrink_icon\\\":\\\"http://i0.hdslb.com/bfs/b/44338bca6bb98a34da40698beb4ee7d19aea92a6.png\\\",\\\"shrink_title\\\":\\\"\\350\\247\\206\\351\\242\\221\\\",\\\"show_status\\\":0,\\\"duration\\\":5000,\\\"arc_type\\\":0}"
                .replace("\\\"", "\"").replace("\\", "\\\\")
        val stringa = JSONObject(a).getString("title")

//        com.google.protobuf.TextFormat.getParser()
//            .merge("a, VideoInfo.getDefaultInstance()", JSONObject(a).toString())
        println(MyLog().protobufToString("148"))

    }

    @Test
    fun tt() {
        val number = "21"
        println(Integer.valueOf(number, 8))

        val valueOf = Integer.valueOf(number, 8)
        val toHexString = Integer.toHexString(valueOf)
        println(toHexString)
    }

    @Test
    fun ta() {
        val str = "\\345\\244\\247\\345\\256\\266\\345\\245\\275\\2728.7"
        val pattern: Pattern = Pattern.compile("\\\\")
        val strs: Array<String> = pattern.split(str)
        val sb = StringBuffer()
        for (s in strs) {
            if (s.trim { it <= ' ' }.isNotEmpty()) {
                val st = Integer.toHexString(Integer.valueOf(s, 8))
                sb.append("%").append(st)
            }
        }
        try {
            println(URLDecoder.decode(sb.toString(), "utf-8"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun getPara() {
        val s = "%20\t \n" +
                "%21\t!\n" +
                "%23\t#\n" +
                "%24\t\$\n" +
                "%25\t%\n" +
                "%26\t&\n" +
                "%27\t'\n" +
                "%28\t(\n" +
                "%29\t)\n" +
                "%2A\t*\n" +
                "%2B\t+\n" +
                "%2C\t,\n" +
                "%2E\t.\n" +
                "%2F\t/\n" +
                "%3A\t:\n" +
                "%3B\t;\n" +
                "%3D\t=\n" +
                "%3F\t?\n" +
                "%40\t@\n" +
                "%5B\t[\n" +
                "%5C\t/\n" +
                "%5D\t]"
        val s8 = StringBuffer()
        s.split("\n").forEach { sa ->
            val replace = sa.split("\t")[0].replace("\t", "").replace("%", "")

            val toOctalString = Integer.toOctalString(Integer.valueOf(replace, 16))
            s8.append(
                ".replace(\"${sa.split("\t")[1]}\",\"\\\\${
                    toOctalString
                }\\\\\")"
            )
        }
        println(s8.toString())
    }


    @Test
    fun printProtobufToString() {
        val s = "成本170万，拍摄周期仅24天，却拍出8.7分的国内第一悬疑片！《心迷宫》"
        //\346\210\220\346\234\2540560\344\270\207\357\274\214\346\213\215\346\221\204\345\221\250\346\234\237\344\273\102444\345\244\251\357\274\214\345\215\264\346\213\215\345\207\5650\1347\345\210\206\347\232\204\345\233\275\345\206\205\347\254\254\344\270\200\346\202\254\347\226\221\347\211\207\357\274\201\343\200\212\345\277\203\350\277\267\345\256\253\343\200\213
        println(protobufToString(s))
    }

    private fun protobufToString(input: String): String {
        val str = URLEncoder.encode(input, "UTF-8").replace(".", "%2E")
        val s = str.substring(1).split("%".toRegex()).toTypedArray()
        val s8 = StringBuffer()
        for (i in s.indices) {
//            if (s[i] != "") {
            s8.append("\\" + Integer.toOctalString(Integer.valueOf(s[i], 16)))
//            }
        }
        return s8.toString()
    }
}