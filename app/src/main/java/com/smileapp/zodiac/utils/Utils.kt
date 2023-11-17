package com.smileapp.zodiac.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.widget.TextView
import java.util.*

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

}