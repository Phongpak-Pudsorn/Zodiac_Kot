package com.starvision

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import java.util.*

object ConstVisionInstallSDK {
    var bSetPacketName = false
    var bSetUrlInstall = false
    var bSetAppBannerID = false
    var bSendContact = false
    var bGetAccount = false

    var strUrlInstall = ""
    var strPacketNameInstall = ""
    var strAppBannerID = ""

    fun getGoogleUrl(strImei: String): String {
        Const.log("ConstVisionInstallSDK","strImei : $strImei")
        return strUrlInstall
    }

    //IMEI
    @SuppressLint("HardwareIds")
    fun getEmi(context : Context) : String{
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ({
            telephonyManager.imei
        }).toString() else ({
            @Suppress("DEPRECATION")
            telephonyManager.deviceId
        }).toString()
    }

    //APP VERSION
    fun getAppVersion(context: Context): String {
        var strVersion = ""
        try {
            val manager = context.packageManager
            val info = manager.getPackageInfo(context.packageName, 0)
            strVersion = info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return strVersion
    }

    //DEVICE NAME
    fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }

    private fun capitalize(s: String?): String {
        if (s == null || s.isEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            first.uppercaseChar().toString() + s.substring(1)
        }
    }

    //API VERSION
    fun getAPIVersion(): String {
        val builder = StringBuilder()
        builder.append(Build.VERSION.RELEASE)
        val fields = Build.VERSION_CODES::class.java.fields
        for (field in fields) {
            val fieldName = field.name
            var fieldValue = -1
            try {
                fieldValue = field.getInt(Any())
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
            if (fieldValue == Build.VERSION.SDK_INT) {
                builder.append("-$fieldName")
            }
        }
        return builder.toString()
    }

    @SuppressLint("HardwareIds")
    fun getUUID(context: Context): String? {
        return try {
            var identifier: String? = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            identifier = UUID.nameUUIDFromBytes(identifier!!.toByteArray(charset("utf8"))).toString()
            identifier
        } catch (e: java.lang.NullPointerException) {
            // Log.e("LOGIN SDK ", "getUUID NullPointerException");
            e.printStackTrace()
            ""
        } catch (e: Exception) {
            // Log.e("LOGIN SDK ", "getUUID Exception");
            e.printStackTrace()
            ""
        }
    }
}