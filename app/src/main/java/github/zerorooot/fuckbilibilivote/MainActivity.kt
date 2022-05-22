package github.zerorooot.fuckbilibilivote

import android.R
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import github.zerorooot.fuckbilibilivote.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var biliSp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        try {
            biliSp = getSharedPreferences("bili", Context.MODE_WORLD_READABLE)
        } catch (e: SecurityException) {
            Toast.makeText(
                this,
                "Please in LSPosed Check this app and restart it !",
                Toast.LENGTH_LONG
            ).show()
            binding.saveButton.isEnabled = false
            binding.hideSwitch.isEnabled = false
            return
        }


        val json = getJson()

        setSpinner(
            binding.biliVersionSpinner,
            json.getJSONArray("biliJson"),
            binding.biliClassName,
            binding.biliClassMethod
        )
        setSpinner(
            binding.hdVersionSpinner,
            json.getJSONArray("hdJson"),
            binding.biliHdClassName,
            binding.biliHdClassMethod
        )


        setTextViewText()
        setButton()

        biliSp.getBoolean("hide", false).let {
            binding.hideSwitch.isChecked = it
        }
        binding.hideSwitch.setOnCheckedChangeListener { _, isChecked ->
            val edit = biliSp.edit()
            edit.putBoolean("hide", isChecked)
            edit.apply()
            hideLauncherIcon(isChecked)
            Toast.makeText(
                application,
                "hide is $isChecked",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun getJson(): JSONObject {
        val file = this.applicationContext.resources.assets.open("init.json").bufferedReader()
            .use { it.readText() }
        val json = JSONObject(file)
        val string = biliSp.getString("json", "")
        if ("" != string) {
            val remoteJson = JSONObject(string!!)

            val biliJsonArray = remoteJson.getJSONArray("biliJson")
            for (i in 0 until biliJsonArray.length()) {
                json.getJSONArray("biliJson").put(biliJsonArray.getJSONObject(i))
            }
            val hdJSONArray = remoteJson.getJSONArray("hdJson")
            for (i in 0 until hdJSONArray.length()) {
                json.getJSONArray("hdJson").put(hdJSONArray.getJSONObject(i))
            }
        }

        return json
    }

    private fun httpConnect() {
        Thread {
            try {
                val url =
                    URL("https://raw.githubusercontent.com/zerorooot/FuckBilibiliVote/main/remote.json")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                val inputStream = BufferedInputStream(connection.inputStream)
                val string = inputStream.bufferedReader().use { it.readText() }
                val editor = biliSp.edit()
                editor.putString("json", string)
                editor.apply()
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        application,
                        "download success,please restart this app",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(application, "download error", Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }
        }.start()
    }

    private fun setButton() {
        binding.saveButton.setOnClickListener {
            save()
            Toast.makeText(applicationContext, "save success", Toast.LENGTH_SHORT).show()
        }
        binding.saveButton.setOnLongClickListener {
            Toast.makeText(application, "start download", Toast.LENGTH_SHORT).show()
            httpConnect()
            true
        }
    }

    private fun setSpinner(
        spinner: Spinner,
        jsonArray: JSONArray,
        className: TextInputEditText,
        classMethodName: TextInputEditText
    ) {

        val array = arrayListOf<String>()
        array.add("customize")

        for (i in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(i)
            array.add(json.getString("version"))
        }

        val adapter = ArrayAdapter(
            this,
            R.layout.simple_spinner_item,
            array
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                try {
                    val json = jsonArray.getJSONObject(position - 1)
                    className.setText(json.getString("className"))
                    classMethodName.setText(json.getString("methodName"))
                } catch (e: Exception) {
                }
            }
        }
    }


    private fun save() {
        val edit = biliSp.edit()
        edit.putString(
            "bili_class_name",
            Objects.requireNonNull(binding.biliClassName.text).toString()
        )
        edit.putString(
            "bili_class_method",
            Objects.requireNonNull(binding.biliClassMethod.text).toString()
        )
        edit.putString(
            "bili_hd_class_name",
            Objects.requireNonNull(binding.biliHdClassName.text).toString()
        )
        edit.putString(
            "bili_hd_class_method",
            Objects.requireNonNull(binding.biliHdClassMethod.text).toString()
        )
        edit.apply()
    }

    private fun hideLauncherIcon(isShow: Boolean) {
        val packageManager = this.packageManager
        val show =
            if (isShow) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        val componentName =
            ComponentName(this@MainActivity, "github.zerorooot.fuckbilibilivote.MainActivityAlias")
        packageManager.setComponentEnabledSetting(
            componentName,
            show,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun setTextViewText() {
        binding.biliClassName.setText(biliSp.getString("bili_class_name", ""))
        binding.biliClassMethod.setText(biliSp.getString("bili_class_method", ""))
        binding.biliHdClassName.setText(biliSp.getString("bili_hd_class_name", ""))
        binding.biliHdClassMethod.setText(biliSp.getString("bili_hd_class_method", ""))
    }
}