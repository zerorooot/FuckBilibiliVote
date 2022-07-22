package github.zerorooot.fuckbilibilivote

import de.robv.android.xposed.XposedHelpers
import org.json.JSONObject

class CommandDmsUtil(private val s: Any) {

    fun getCommand(): String {
        return try {
            template("getCommand").toString().replace("#", "")
        } catch (e: Exception) {
            ""
        }
    }

    fun getContent(): String {
        return try {
            template("getContent").toString()
        } catch (e: Exception) {
            ""
        }
    }

    fun getExtra(): JSONObject {
        return try {
            JSONObject(template("getExtra").toString())
        } catch (e: Exception) {
            JSONObject()
        }
    }

    fun getProgress(): Int {
        return try {
            template("getProgress").toString().toInt()
        } catch (e: Exception) {
            0
        }
    }


    private fun template(name: String): Any {
        return XposedHelpers.callMethod(s, name)
    }
}