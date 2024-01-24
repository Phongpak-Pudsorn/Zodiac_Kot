package com.smileapp.zodiac.view.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.window.SplashScreen
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.smileapp.zodiac.R
import com.smileapp.zodiac.databinding.ActivityMainBinding
import com.smileapp.zodiac.pushnotification.MyFirebaseMessagingService
import com.smileapp.zodiac.utils.Utils
import com.starvision.ConstVisionInstallSDK
import com.starvision.ccusdk.StarVisionCcuSDK
import com.starvision.installsdk.StarVisionInstallSDK
import java.util.*

class MainActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        Log.e("main", "close app")
        finishAndRemoveTask()
    }
    val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
        Utils.UUID = getUUID(this)
        Utils.AppPreference(this)
        Utils.setAdvertisingIdClient(Utils.UUID)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
//                    Log.e("FirebaseInstanceId", "GetInstanceId failed " + task.exception)
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result
//                Log.d("token", token!!)
                Utils.Token = token
//                FileHelper.saveToFile("MyFirebaseMessagingService: $token")
//                Log.e("PushRegis", Url.starBase + FirebaseMessagingService().genRegisUrl(Consts.UUID,token))
                MyFirebaseMessagingService().CallSendingToken(token)
            })

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
        Log.e("main","onPause")
        handler.postDelayed(runnable,5*60*1000)
        super.onPause()
    }

    override fun onStop() {
        Log.e("main","onStop")
        super.onStop()
    }

    override fun onResume() {
        Log.e("main","onResume")
        handler.removeCallbacks(runnable)
        super.onResume()
    }
}