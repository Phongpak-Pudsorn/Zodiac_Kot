package com.smileapp.zodiac.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.util.Log
import android.widget.TextView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

object Utils {
    private val namePreferences = "zdaadmob"
    private val modePreferences = Context.MODE_PRIVATE
    private var sharedPrefs: SharedPreferences? = null
    private var prefsEditor: SharedPreferences.Editor? = null
    const val KEY_STATUS_PUSNOTIFICATION = "Status"
    const val APP_SHARED_PREFS = "mAppPreferences"
    const val KEY_STATUSNOTIC = "KEY_STATUSNOTIC"
    const val KEY_ACCEPT = "isAccept"
    const val KEY_FIRST_OPEN = "isFirstTime"
    const val NAME_USER = "NAME_USER"
    const val KEY_RASI = "KEY_RASI"
    const val KEY_GENDER = "KEY_GENDER"
    const val NAME_DATE_RASI = "NAME_DATE_RASI"
    const val KEY_NOTICEADS = "KEY_NOTICEADS"
    const val ADVERTISING_ID_CLIENT = "ADVERTISING_ID_CLIENT"
    const val NAME_MENU_MONTH ="NAME_MENU_MONTH"
    const val NAME_MENU_YEAR ="NAME_MENU_YEAR"
    var UUID = ""
    var currentFragment = 0

    fun AppPreference(context: Context) {
//    	Log.e("TAG", APP_SHARED_PREFS);
        sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE)
        prefsEditor = sharedPrefs!!.edit()

    }
    fun getStatusPushNoticfication(): Boolean? {
        return sharedPrefs!!.getBoolean(KEY_STATUS_PUSNOTIFICATION, false)
    }
    fun setStatusNotic(status:Boolean){
        prefsEditor!!.putBoolean(KEY_STATUSNOTIC,status)
        prefsEditor!!.commit()
    }
    fun getStatusNotic(): Boolean {
        return sharedPrefs!!.getBoolean(KEY_STATUSNOTIC,true)
    }
    fun setNameUser(name:String){
        prefsEditor!!.putString(NAME_USER,name)
        prefsEditor!!.commit()

    }
    fun getNameUser():String{
        return sharedPrefs!!.getString(NAME_USER,"").toString()
    }

    //---------END  BANNER POPUP  SIZE-------------------
    fun setAdvertisingIdClient(id: String?) {
        prefsEditor!!.putString(ADVERTISING_ID_CLIENT, id)
        prefsEditor!!.commit()
    }

    fun getAdvertisingIdClient(): String? {
        return sharedPrefs!!.getString(ADVERTISING_ID_CLIENT, "")
    }
    //---------END AdvertisingIdClient ID -------------------


    //--------- AdvertisingIdClient ID -------------------
    fun loadFromAssets(mContext: Context): String? {
        var dataStr = ""
        try {
            val input = mContext.assets.open("DataZodiac/Data_Zodiac.json")
            val size = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            input.close()
            dataStr = String(buffer, charset("UTF-8"))

        }catch (ex: IOException){
            ex.printStackTrace()
            return null
        }
        return dataStr
    }

    //---------END  SET ID RASI-------------------
    fun setRasi(month: Int) {
        prefsEditor!!.putInt(KEY_RASI, month)
        prefsEditor!!.commit()
    }

    fun getRasi(): Int {
        return sharedPrefs!!.getInt(KEY_RASI, 0)
    }
    fun setGENDER(Gender: String?) {
        prefsEditor!!.putString(KEY_GENDER, Gender)
        prefsEditor!!.commit()
    }

    fun getGENDER(): String? {
        return sharedPrefs!!.getString(KEY_GENDER, "")
    }

    //---------END  SET Name User-------------------

    fun setNameAndDateRasi(date: String?) {
        prefsEditor!!.putString(NAME_DATE_RASI, date)
        prefsEditor!!.commit()
    }

    fun getNameAndDateRasi(): String? {
        return sharedPrefs!!.getString(NAME_DATE_RASI, "")
    }
    fun setNoticeAds(status: Boolean){
        prefsEditor!!.putBoolean(KEY_NOTICEADS,status)
        prefsEditor!!.commit()
    }
    fun getNoticeAds(): Boolean {
        return sharedPrefs!!.getBoolean(KEY_NOTICEADS,true)
    }
    fun setNameMenuMonth(name:String){
        prefsEditor!!.putString(NAME_MENU_MONTH,name)
        prefsEditor!!.commit()
    }
    fun getNameMenuMonth():String? {
        return sharedPrefs!!.getString(NAME_MENU_MONTH,"")
    }
    fun setNameMenuYear(name:String){
        prefsEditor!!.putString(NAME_MENU_YEAR,name)
        prefsEditor!!.commit()
    }
    fun getNameMenuYear():String? {
        return sharedPrefs!!.getString(NAME_MENU_YEAR,"")
    }

    @Throws(Exception::class)
    fun doHttpUrlConnectionAction(desiredUrl: String?): String? {
        var url: URL? = null
        var reader: BufferedReader? = null
        val stringBuilder: StringBuilder
        var strHttp = ""
        return try {
            // create the HttpURLConnection
            url = URL(desiredUrl)
            val connection = url.openConnection() as HttpsURLConnection
            // just want to do an HTTP GET here
            connection.requestMethod = "GET"
            // connection.setReadTimeout(5000);
            connection.connectTimeout = 10000
            connection.connect()
            // read the output from the server
            reader = BufferedReader(InputStreamReader(connection.inputStream, "UTF-8"))
            stringBuilder = StringBuilder()
            var line: String? = null
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(
                    """
                        $line
                        
                        """.trimIndent()
                )
            }
            strHttp = stringBuilder.toString()
            strHttp
        } catch (e: Exception) {
            Log.e("timeout", "timeout")
            strHttp = ""
            e.printStackTrace()
            strHttp
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (ioe: IOException) {
                    ioe.printStackTrace()
                }
            }
        }
    }
    fun setPrefer(mcontext: Context, key:String, objects:Any){
        val preferences = mcontext.getSharedPreferences(namePreferences, modePreferences)
        val prefer = preferences.edit()
        when(objects){
            is String -> {
//                Log.e("encoded","before: "+objects)
                val encoded: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Base64.getEncoder().encodeToString(objects.toByteArray())
                } else {
                    android.util.Base64.encodeToString(objects.toByteArray(),0)
                }
//                Log.e("encoded","after: "+encoded)
                prefer.putString(key,encoded)
            }
            is Int -> {
                prefer.putInt(key,objects)
            }
            is Boolean -> {
                prefer.putBoolean(key,objects)
            }
            is Float -> {
                prefer.putFloat(key,objects)
            }
            is Long -> {
                prefer.putLong(key,objects)
            }
        }
        prefer.apply()
    }
    fun getPrefer(mcontext: Context, key: String, defaultObj:Any):Any?{
        val preferences = mcontext.getSharedPreferences(namePreferences, modePreferences)
        when(defaultObj){
            is String -> {
                val prefer = preferences.getString(key,defaultObj)
                val decoded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String(Base64.getDecoder().decode(prefer.toString()))
                } else {
                    android.util.Base64.decode(prefer,0).toString()
                }
                return  decoded
            }
            is Int -> {
                return  preferences.getInt(key,defaultObj)
            }
            is Boolean -> {
                return  preferences.getBoolean(key,defaultObj)
            }
            is Float -> {
                return  preferences.getFloat(key,defaultObj)
            }
            is Long -> {
                return  preferences.getLong(key,defaultObj)
            }
        }
        return null
    }
    fun setTextGradient_Blue(tv:TextView){
        val myShader: Shader = LinearGradient(
            0f, 70f, 0f, 180f,
            Color.WHITE, Color.BLUE,
            Shader.TileMode.CLAMP
        )
        tv.paint.shader = myShader
    }
    fun setTextGradient_Green(tv:TextView){
        val myShader: Shader = LinearGradient(
            0f, 50f, 0f, 200f,
            Color.WHITE, Color.GREEN,
            Shader.TileMode.CLAMP
        )
        tv.paint.shader = myShader
    }

}