package github.zerorooot.fuckbilibilivote

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
            binding.autoHookSwitch.isEnabled = false
            return
        }



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

        biliSp.getBoolean("auto_hook", true).let {
            binding.autoHookSwitch.isChecked = it
            setVisibility(it)
        }
        binding.autoHookSwitch.setOnCheckedChangeListener { _, isChanged ->
            val edit = biliSp.edit()
            edit.putBoolean("auto_hook", isChanged)
            edit.apply()
            setVisibility(isChanged)
        }
    }
    private fun setVisibility(isVisibility: Boolean) {
        val visibility = if (isVisibility) View.GONE else View.VISIBLE
        binding.biliClassName.visibility = visibility
        binding.biliClassMethod.visibility = visibility
        binding.saveButton.visibility = visibility
        binding.textInputLayout.visibility = visibility
        binding.textInputLayout2.visibility = visibility
    }


    private fun setButton() {
        binding.saveButton.setOnClickListener {
            save()
            Toast.makeText(applicationContext, "save success", Toast.LENGTH_SHORT).show()
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
    }
}