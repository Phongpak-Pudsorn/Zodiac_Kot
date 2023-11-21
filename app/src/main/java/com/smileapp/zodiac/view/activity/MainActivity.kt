package com.smileapp.zodiac.view.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.smileapp.zodiac.R
import com.smileapp.zodiac.databinding.ActivityMainBinding
import com.smileapp.zodiac.utils.Utils
import com.starvision.ConstVisionInstallSDK
import com.starvision.ccusdk.StarVisionCcuSDK
import com.starvision.installsdk.StarVisionInstallSDK
import java.util.*

class MainActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        Log.e("Run", "close app")
        this.recreate()
        finish()
    }
    val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
        Utils.UUID = getUUID(this)
        setStarVisionSDK()
        Utils.AppPreference(this)
        Utils.setAdvertisingIdClient(Utils.UUID)
    }
    fun setStarVisionSDK(){
        val starVisionCcuSDK = StarVisionCcuSDK(this)
        val dot = "."
        val arr = packageName.split(dot).toTypedArray()
        starVisionCcuSDK.startService("8888", ConstVisionInstallSDK.getUUID(this)!!, packageName, arr[2])

        val callWebServerSendContact = StarVisionInstallSDK(this)
        callWebServerSendContact.setUrlInstall("https://starvision.in.th/appbannersdk/serverweb/sdk_app_install.php");
        callWebServerSendContact.setPacketNameInstall(getPackageName())
        callWebServerSendContact.setAppBannerID("155")
        callWebServerSendContact.setSendContact(false)
        callWebServerSendContact.setGetAccount(false)
        callWebServerSendContact.startService()
    }
    fun getUUID(context: Context):String{
        try {
            var  identifier =""
            identifier = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            identifier = UUID.nameUUIDFromBytes(identifier.toByteArray(charset("utf8"))).toString()
            return identifier
        }catch (e:java.lang.NullPointerException){

        }
        return ""
    }
    override fun onPause() {
        super.onPause()
        Log.e("main","onPause")
        handler.postDelayed(runnable,5*60*1000)
    }

    override fun onStop() {
        super.onStop()
        Log.e("main","onStop")
    }

    override fun onResume() {
        super.onResume()
        Log.e("main","onResume")
        handler.removeCallbacks(runnable)
    }
}